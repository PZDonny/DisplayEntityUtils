package net.donnypz.displayentityutils.database;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public final class MongoDisplayStorage implements DBDisplayStorage {
    private static MongoClient client;
    private static MongoDatabase database;

    private static MongoCollection<Document> GROUP_COLLECTION;
    private static final String GROUP_FIELD = "displayGroup";
    private static final String GROUP_DISPLAY_NAME = "display entity group";

    private static MongoCollection<Document> ANIMATION_COLLECTION;
    private static final String ANIMATION_FIELD = "displayAnimation";
    private static final String ANIMATION_DISPLAY_NAME = "animation";

    private static final String TAG_FIELD = "tag";

    private static boolean isConnected = false;

    @Override
    public boolean saveDisplayEntityGroup(@NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        if (!isConnected){
            return false;
        }
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(displayEntityGroup);

            gzipOut.close();
            objOut.close();
            byte[] data = byteOut.toByteArray();
            Document doc = new Document();

            doc.append("tag", displayEntityGroup.getTag())
                .append("displayGroup", data);

            Document existing = getGroupDocument(displayEntityGroup.getTag());
            if (existing != null){
                if (DisplayConfig.overwritexistingSaves()){
                    Bson updateOperation = new Document ("$set", doc);
                    GROUP_COLLECTION.updateOne(existing, updateOperation);
                }
                else{
                    if (saver != null){
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display entity group to MongoDB!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }

            }
            else{
                GROUP_COLLECTION.insertOne(doc);
            }

            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved display entity group to MongoDB!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display entity group to MongoDB!"));
            }
            return false;
        }
    }

    @Override
    public void deleteDisplayEntityGroup(@NotNull String tag, @Nullable Player deleter){
        if (!isConnected()) return;
        DisplayAPI.getScheduler().runAsync(() -> {
            Document doc = getGroupDocument(tag);
            if (doc != null){
                GROUP_COLLECTION.deleteOne(doc);
                if (deleter != null){

                    deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted group from MongoDB!"));
                    return;
                }
            }
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved display entity group does not exist in MongoDB database!"));
            }
        });
    }

    @Override
    public @Nullable DisplayEntityGroup getDisplayEntityGroup(@NotNull String tag){
        if (!isConnected) return null;
        Document doc = getGroupDocument(tag);
        if (doc == null){
            return null;
        }
        byte[] bytes = ((Binary) doc.get("displayGroup")).getData();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return DisplayGroupManager.getGroup(in);
    }


    @Override
    public boolean saveDisplayAnimation(@NotNull DisplayAnimation displayAnimation, @Nullable Player saver){
        if (!isConnected){
            return false;
        }
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(displayAnimation);
            gzipOut.close();
            objOut.close();

            byte[] data = byteOut.toByteArray();
            Document doc = new Document();

            doc.append("tag", displayAnimation.getAnimationTag())
                    .append("displayAnimation", data);

            Document existing = getAnimationDocument(displayAnimation.getAnimationTag());
            if (existing != null){
                if (DisplayConfig.overwritexistingSaves()){
                    Bson updateOperation = new Document ("$set", doc);
                    ANIMATION_COLLECTION.updateOne(existing, updateOperation);
                }
                else{
                    if (saver != null){
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save animation to MongoDB!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }

            }
            else{
                ANIMATION_COLLECTION.insertOne(doc);
            }

            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved animation to MongoDB!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save animation to MongoDB!"));
            }
            return false;
        }
    }

    @Override
    public void deleteDisplayAnimation(@NotNull String tag, @Nullable Player deleter){
        if (!isConnected()) return;
        DisplayAPI.getScheduler().runAsync(() -> {
            Document doc = getAnimationDocument(tag);
            if (doc != null){
                ANIMATION_COLLECTION.deleteOne(doc);
                if (deleter != null){
                    deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted animation from MongoDB database!"));
                    return;
                }
            }
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved animation does not exist in MongoDB database!"));
            }
        });
    }

    @Override
    public @Nullable DisplayAnimation getDisplayAnimation(@NotNull String tag){
        if (!isConnected){
            return null;
        }
        Document doc = getAnimationDocument(tag);
        if (doc == null){
            return null;
        }
        byte[] bytes = ((Binary) doc.get("displayAnimation")).getData();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return DisplayAnimationManager.getAnimation(in);
    }


    @Override
    public @NotNull List<String> getGroupTags(){
        return getTags(GROUP_COLLECTION);
    }

    @Override
    public @NotNull List<String> getAnimationTags(){
        return getTags(ANIMATION_COLLECTION);

    }

    private List<String> getTags(MongoCollection<Document> collection){
        if (!isConnected()) return Collections.emptyList();
        List<String> tags = new ArrayList<>();
        for(Document doc : collection.find()){
            tags.add(doc.getString("tag"));
        }
        return tags;
    }

    @ApiStatus.Internal
    public void createConnection(
            String connectionString,
            String databaseName,
            String groupColl,
            String animColl
    ) {
        if (isConnected()){
            return;
        }
        if (databaseName.isEmpty() || groupColl.isBlank() || animColl.isBlank()){
            Bukkit.getConsoleSender().sendMessage(Component.text("There was an error connecting to the MongoDB Database! Database and/or Collection names are empty!", NamedTextColor.RED));
            isConnected = false;
            return;
        }

        DisplayAPI.getScheduler().runAsync(() -> {
            try{
                ConnectionString cString = new ConnectionString(connectionString);
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(cString)
                        .serverApi(ServerApi.builder()
                                .version(ServerApiVersion.V1)
                                .build())
                        .build();

                client = MongoClients.create(settings);
                database = client.getDatabase(databaseName);

                createIfNotExisting(groupColl);
                createIfNotExisting(animColl);

                GROUP_COLLECTION = database.getCollection(groupColl);
                ANIMATION_COLLECTION = database.getCollection(animColl);

                Bukkit.getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<aqua>Successfully connected to <green>MongoDB!")));
                isConnected = true;
            }
            catch (IllegalArgumentException | MongoException e){
                isConnected = false;
                Bukkit.getConsoleSender().sendMessage(Component.text("There was an error connecting to the MongoDB Database!", NamedTextColor.RED));
                e.printStackTrace();
            }
        });
    }

    private void createIfNotExisting(String collectionName){
        boolean contains = false;
        for (String s : database.listCollectionNames()){
            if (s.equals(collectionName)){
                contains = true;
                break;
            }
        }
        if (!contains){
            database.createCollection(collectionName);
            GROUP_COLLECTION = database.getCollection(collectionName);
        }
    }

    @Override
    public void closeConnection(){
        if (client == null || !isConnected){
            return;
        }
        try{
            client.close();
            isConnected = false;
        }
        catch(MongoException e){
            isConnected = false;
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(Component.text("There was an error closing the connection to the MongoDB Database", NamedTextColor.RED));
        }
    }

    @Override
    public boolean isConnected(){
        return isConnected;
    }

    private Document getGroupDocument(String tag){
        return GROUP_COLLECTION.find(new Document("tag", tag)).first();
    }

    private Document getAnimationDocument(String tag){
        return ANIMATION_COLLECTION.find(new Document("tag", tag)).first();
    }

}
