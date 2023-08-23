package com.pzdonny.displayentityutils.managers;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class MongoManager{
    private static MongoClient client;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static boolean isConnected = false;



    private MongoManager(){}

    static boolean saveDisplayEntityGroup(DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        if (!isConnected){
            return false;
        }
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(displayEntityGroup);
            byte[] data = byteOut.toByteArray();
            Document doc = new Document();

            doc.append("tag", displayEntityGroup.getTag())
                .append("displayGroup", data);

            Document existing = getDocument(displayEntityGroup.getTag());
            if (existing != null){
                if (DisplayEntityPlugin.overrideExistingSaves()){
                    Bson updateOperation = new Document ("$set", doc);
                    collection.updateOne(existing, updateOperation);
                }
                else{
                    if (saver != null){
                        saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group to MongoDB!");
                        saver.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Save with tag already exists!");
                    }
                    return false;
                }

            }
            else{
                collection.insertOne(doc);
            }

            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- "+ ChatColor.GREEN + "Successfully saved display entity group to MongoDB!");
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group to MongoDB!");
            }
            return false;
        }
    }

    static void deleteDisplayEntityGroup(String tag, @Nullable Player deleter){
        if (!DisplayEntityPlugin.isMongoEnabled() || !isConnected()) return;
        new BukkitRunnable(){
            @Override
            public void run() {
                Document doc = getDocument(tag);
                if (doc != null){
                    collection.deleteOne(doc);
                    if (deleter != null){
                        deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.LIGHT_PURPLE+"Successfully deleted from MongoDB!");
                        return;
                    }
                }
                if (deleter != null){
                    deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Entity does not exist in MongoDB database!");
                }
            }
        }.runTaskAsynchronously(DisplayEntityPlugin.getInstance());
    }

    static DisplayEntityGroup retrieveDisplayEntityGroup(String tag){
        if (!DisplayEntityPlugin.isMongoEnabled() || !isConnected) return null;
        Document doc = getDocument(tag);
        if (doc == null){
            return null;
        }
        byte[] bytes = ((Binary) doc.get("displayGroup")).getData();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try{
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (DisplayEntityGroup) objIn.readObject();
        }
        catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    static List<String> getDisplayEntityTags(){
        List<String> tags = new ArrayList<>();
        if (!DisplayEntityPlugin.isMongoEnabled() || !isConnected()) return tags;
        for(Document doc : collection.find()){
            tags.add(doc.getString("tag"));
        }
        return tags;
    }

    public static void createConnection(String connectionString, String databaseName, String collectionName) {
        if (isConnected()) return;
        if (databaseName.isEmpty() || collectionName.isEmpty()){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error connecting to the MongoDB Database! Database and/or Collection name is empty!");
            isConnected = false;
            return;
        }
        new BukkitRunnable(){
            @Override
            public void run() {
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
                    collection = database.getCollection(collectionName);
                    //Automatically generate collection if it doesn't exist
                    boolean contains = false;
                    for (String s : database.listCollectionNames()){
                        if (s.equals(collectionName)){
                            contains = true;
                            break;
                        }
                    }
                    if (!contains){
                        database.createCollection(collectionName);
                        collection = database.getCollection(collectionName);
                    }

                    Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.AQUA+"Successfully connected to"+ChatColor.GREEN+ " MongoDB!");
                    isConnected = true;
                }catch (IllegalArgumentException | MongoException e){
                    isConnected = false;
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error connecting to the MongoDB Database!");
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(DisplayEntityPlugin.getInstance());
    }

    public static void closeConnection(){
        if (!DisplayEntityPlugin.isMongoEnabled() || client == null || !isConnected) return;
        try{
            client.close();
            isConnected = false;
        }
        catch(MongoException e){
            isConnected = false;
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error closing the connection to the MongoDB Database");
        }
    }

    /**
     * Check whether MongoDB is connected
     * @return boolean of MongoDB connection status
     */
    public static boolean isConnected(){
        return isConnected;
    }

    private static Document getDocument(String tag){
        return collection.find(new Document("tag", tag)).first();
    }

}
