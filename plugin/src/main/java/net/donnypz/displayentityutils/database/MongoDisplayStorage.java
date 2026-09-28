package net.donnypz.displayentityutils.database;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public final class MongoDisplayStorage implements DBDisplayStorage {

    private static MongoCollection<Document> GROUP_COLLECTION;
    private static final String GROUP_FIELD = "displayGroup";
    private static final String GROUP_DISPLAY_NAME = "display entity group";

    private static MongoCollection<Document> ANIMATION_COLLECTION;
    private static final String ANIMATION_FIELD = "displayAnimation";
    private static final String ANIMATION_DISPLAY_NAME = "animation";

    private static final String TAG_FIELD = "tag";

    private MongoClient client;
    private MongoDatabase database;
    private boolean isConnected = false;

    public void createConnection(
            String host,
            int port,
            String databaseName,
            String username,
            String password,
            String groupCollection,
            String animationCollection
    ){
        String connectionString = String.format(
                "mongodb://%s:%s@%s:%d/%s",
                username,
                password,
                host,
                port,
                databaseName
        );

        this.createConnection(
                connectionString,
                databaseName,
                groupCollection,
                animationCollection
        );
    }

    public void createConnection(
            String connectionString,
            String databaseName,
            String groupCollection,
            String animationCollection
    ) {
        if (!this.canConnect(databaseName, groupCollection, animationCollection)) return;

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

                this.createIfNotExisting(groupCollection);
                this.createIfNotExisting(animationCollection);

                GROUP_COLLECTION = database.getCollection(groupCollection);
                ANIMATION_COLLECTION = database.getCollection(animationCollection);

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

    private boolean canConnect(
            String databaseName,
            String groupCollection,
            String animationCollection
    ){
        if (isConnected()) return false;

        if (databaseName.isBlank() || groupCollection.isBlank() || animationCollection.isBlank()){
            Bukkit.getConsoleSender().sendMessage(Component.text("There was an error connecting to the MongoDB Database! Database and/or Collection names are empty!", NamedTextColor.RED));
            isConnected = false;
            return false;
        }

        return true;
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
    public boolean isConnected(){
        return isConnected;
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
    public boolean saveDisplayEntityGroup(@NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        String tag = displayEntityGroup.getTag();
        return saveEntity(
                tag,
                displayEntityGroup,
                GROUP_COLLECTION,
                GROUP_FIELD,
                "display entity group",
                saver);
    }

    @Override
    public void deleteDisplayEntityGroup(@NotNull String tag, @Nullable Player deleter){
        deleteEntity(
                tag,
                GROUP_COLLECTION,
                GROUP_DISPLAY_NAME,
                deleter
        );
    }

    @Override
    public @Nullable DisplayEntityGroup getDisplayEntityGroup(@NotNull String tag){
        if (!isConnected) return null;
        Document doc = getGroupDocument(tag);
        if (doc == null){
            return null;
        }
        byte[] bytes = ((Binary) doc.get(GROUP_FIELD)).getData();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return DisplayGroupManager.getGroup(in);
    }


    @Override
    public boolean saveDisplayAnimation(@NotNull DisplayAnimation displayAnimation, @Nullable Player saver){
        String tag = displayAnimation.getAnimationTag();
        return saveEntity(
                tag,
                displayAnimation,
                ANIMATION_COLLECTION,
                ANIMATION_FIELD,
                "animation",
                saver
        );
    }

    @Override
    public void deleteDisplayAnimation(@NotNull String tag, @Nullable Player deleter){
        deleteEntity(
                tag,
                ANIMATION_COLLECTION,
                ANIMATION_DISPLAY_NAME,
                deleter
        );
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
        byte[] bytes = ((Binary) doc.get(ANIMATION_FIELD)).getData();
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


    private boolean saveEntity(
            String tag,
            Object entity,
            MongoCollection<Document> collection,
            String fieldName,
            String displayName,
            Player saver){
        if (!isConnected) return false;
        try{
            byte[] data = CommonDisplayStorageUtils.toByteArray(entity);
            Document doc = new Document();

            doc
                    .append(TAG_FIELD, tag)
                    .append(fieldName, data);

            Document existing = getGroupDocument(tag);
            if (existing != null){
                if (DisplayConfig.overwritexistingSaves()){
                    Bson updateOperation = new Document ("$set", doc);
                    collection.updateOne(existing, updateOperation);
                }
                else{
                    if (saver != null){
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save "+displayName+" to MongoDB!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }

            }
            else{
                collection.insertOne(doc);
            }

            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved "+displayName+" to MongoDB!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save "+displayName+" to MongoDB!"));
            }
            return false;
        }
    }

    private void deleteEntity(
            String tag,
            MongoCollection<Document> collection,
            String displayName,
            Player deleter
    ){
        if (!isConnected()) return;
        Document doc = getGroupDocument(tag);
        if (doc != null){
            collection.deleteOne(doc);
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted "+displayName+" from MongoDB!"));
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved "+displayName+" does not exist in MongoDB database!"));
        }
    }

    private List<String> getTags(MongoCollection<Document> collection){
        if (!isConnected()) return new ArrayList<>();

        return collection.find()
                .projection(
                        Projections.fields(
                                Projections.include(TAG_FIELD),
                                Projections.excludeId()
                        )
                )
                .map(doc -> doc.getString(TAG_FIELD))
                .into(new ArrayList<>());
    }

    private Document getGroupDocument(String tag){
        return GROUP_COLLECTION.find(new Document(TAG_FIELD, tag))
                .first();
    }

    private Document getAnimationDocument(String tag){
        return ANIMATION_COLLECTION.find(new Document(TAG_FIELD, tag))
                .first();
    }

}
