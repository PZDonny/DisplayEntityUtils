package com.pzdonny.displayentityutils.managers;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MYSQLManager {

    private static boolean isConnected = false;
    private static Connection connection;

    public static void createConnection(String host, int port, String database, String username, String password, boolean usessl){
        if (isConnected) return;
        new BukkitRunnable(){
            @Override
            public void run() {
                String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true&allowMultiQueries=true&useSSL="+usessl;
                try{
                    //Establish Connection
                    connection = DriverManager.getConnection(url, username, password);

                    //Create Default Table
                    Statement statement = connection.createStatement();
                    String tableCreator = "CREATE TABLE IF NOT EXISTS saved_displays(tag VARCHAR(100) UNIQUE, display_group BLOB)";
                    statement.execute(tableCreator);

                    Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.AQUA+"Successfully connected to"+ChatColor.BLUE+ " MYSQL!");
                    isConnected = true;
                }
                catch(SQLException e){
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error connecting to the MYSQL database");
                    isConnected = false;
                }
            }
        }.runTaskAsynchronously(DisplayEntityPlugin.getInstance());
    }

    public static void createConnection(String url){
        if (isConnected) return;
        new BukkitRunnable(){
            @Override
            public void run() {
                try{
                    //Establish Connection
                    connection = DriverManager.getConnection(url);

                    //Create Default Table
                    Statement statement = connection.createStatement();
                    String tableCreator = "CREATE TABLE IF NOT EXISTS saved_displays(tag VARCHAR(100) UNIQUE primary key, display_group BLOB)";
                    statement.execute(tableCreator);

                    Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.AQUA+"Successfully connected to"+ChatColor.BLUE+ " MYSQL!");
                    isConnected = true;
                }
                catch(SQLException e){
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error connecting to the MYSQL database");
                    isConnected = false;
                }
            }
        }.runTaskAsynchronously(DisplayEntityPlugin.getInstance());
    }

    public static void closeConnection(){
        if (connection == null || !isConnected) return;
        try{
            connection.close();
            isConnected = false;
        }
        catch(SQLException e){
            isConnected = false;
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error closing the connection to the MYSQL Database");
        }
    }

    /**
     * Check whether MySQL is connected
     * @return boolean of MySQL connection status
     */
    public static boolean isConnected() {
        return isConnected;
    }

    static boolean saveDisplayEntityGroup(DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        if (!isConnected){
            return false;
        }
        try{
            String tag = displayEntityGroup.getTag();
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(displayEntityGroup);
            byte[] data = byteOut.toByteArray();
            ByteArrayInputStream blobStream = new ByteArrayInputStream(data);

            String save = "INSERT INTO saved_displays VALUES(\""+tag+"\", ?)";
            PreparedStatement statement = connection.prepareStatement(save);
            statement.setBlob(1, blobStream);
            if (retrieveDisplayEntityGroup(tag) != null){
                if (DisplayEntityPlugin.overrideExistingSaves()){
                    deleteDisplayEntityGroup(tag, null);
                }
                else{
                    if (saver != null) {
                        saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group to MYSQL!");
                        saver.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Save with tag already exists!");
                    }
                    return false;
                }
            }
            statement.executeUpdate();
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- "+ ChatColor.GREEN + "Successfully saved display entity group to MYSQL!");
            }
            return true;
        }
        catch(SQLException | IOException e){
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group to MYSQL!");
            }
            e.printStackTrace();
            return false;
        }
    }

    static void deleteDisplayEntityGroup(String tag, @Nullable Player deleter){
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()) return;
        try{
            ResultSet results = getSingleResult(tag);
            if (results == null || !results.next()){
                if (deleter != null){
                    deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Entity does not exist in MYSQL database!");
                }
                return;
            }

            Statement statement = connection.createStatement();
            String delete = "DELETE FROM saved_displays WHERE tag = \""+tag+"\"";
            statement.execute(delete);
            if (deleter != null){
                deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.LIGHT_PURPLE+"Successfully deleted from MYSQL!");
            }
        }
        catch(SQLException ignored){
        }

    }

    static DisplayEntityGroup retrieveDisplayEntityGroup(String tag){
        if (!isConnected()) return null;
        try{
            ResultSet results = getSingleResult(tag);
            if (results == null || !results.next()) return null;
            Blob blob = results.getBlob("display_group");
            ByteArrayInputStream in = new ByteArrayInputStream(blob.getBinaryStream().readAllBytes());
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (DisplayEntityGroup) objIn.readObject();
        }
        catch(SQLException | IOException | ClassNotFoundException e){
            //e.printStackTrace();
            return null;
        }
    }

    static List<String> getDisplayEntityTags(){
        List<String> tags = new ArrayList<>();
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()) return tags;
        ResultSet results = getAllResults();
        try{
            if (results == null) return tags;
            while(results.next()){
                tags.add(results.getString("tag"));
            }
            return tags;
        }
        catch(SQLException e){
            return tags;
        }
    }

    private static ResultSet getSingleResult(String tag){
        try{
            Statement statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_displays WHERE tag = \""+tag+"\"";
            return statement.executeQuery(retrieve);
        }catch(SQLException e){
            return null;
        }
    }

    private static ResultSet getAllResults(){
        try{
            Statement statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_displays";
            return statement.executeQuery(retrieve);
        }catch(SQLException e){
            return null;
        }
    }
}
