package net.donnypz.displayentityutils.managers;

import com.zaxxer.hikari.HikariDataSource;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.dbutils.DbUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;


public final class MYSQLManager {

    private static boolean connected = false;
    private static HikariDataSource dataSource;

    private MYSQLManager(){}
    public static void createConnection(String host, int port, String database, String username, String password, boolean usessl){
        if (connected){
            return;
        }
        String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true&allowMultiQueries=true&useSSL="+usessl;
        createConnection(url, username, password);
    }

    public static void createConnection(String url, String username, String password){
        if (connected){
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            try {
                //Set Data Source
                dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(url);
                dataSource.setUsername(username);
                dataSource.setPassword(password);

                dataSource.setMinimumIdle(3);
                dataSource.setMaximumPoolSize(6);

                //Test Connection
                Connection connection = dataSource.getConnection();


                //Create Default Table
                Statement statement = connection.createStatement();
                String groupCreator = "CREATE TABLE IF NOT EXISTS saved_displays(tag VARCHAR(128) UNIQUE, display_group BLOB)";
                statement.execute(groupCreator);
                String animCreator = "CREATE TABLE IF NOT EXISTS saved_animations(tag VARCHAR(128) UNIQUE, display_anim BLOB)";
                statement.execute(animCreator);


                DbUtils.closeQuietly(statement);
                DbUtils.closeQuietly(connection);


                Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.AQUA+"Successfully connected to" + ChatColor.BLUE + " MYSQL!");
                connected = true;
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"There was an error connecting to the MYSQL database");
                closeConnection();
            }
        });
    }

    public static void closeConnection(){
        try{
            if (dataSource != null){
                dataSource.close();
            }
        }
        /*catch(SQLException e){
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error closing the connection to the MYSQL Database");
        }*/
        finally {
            connected = false;
            dataSource = null;
        }
    }

    /**
     * Check whether MySQL is connected
     * @return boolean of MySQL connection status
     */
    public static boolean isConnected() {
        return connected;
    }

    private static Connection getConnection(){
        try{
            return dataSource.getConnection();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    static boolean saveDisplayEntityGroup(DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        if (!connected){
            return false;
        }
        PreparedStatement statement = null;
        Connection connection = null;
        try{
            String tag = displayEntityGroup.getTag();
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(displayEntityGroup);

            gzipOut.close();
            objOut.close();

            byte[] data = byteOut.toByteArray();

            ByteArrayInputStream blobStream = new ByteArrayInputStream(data);

            String save = "INSERT INTO saved_displays VALUES(\""+tag+"\", ?);";
            connection = getConnection();
            statement =  connection.prepareStatement(save);
            statement.setBlob(1, blobStream);
            if (retrieveDisplayEntityGroup(tag) != null){
                if (DisplayEntityPlugin.overwritexistingSaves()){
                    deleteDisplayEntityGroup(tag, null);
                }
                else{
                    if (saver != null) {
                        saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group to MYSQL!");
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }
            }
            statement.executeUpdate();
            blobStream.close();
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
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    static void deleteDisplayEntityGroup(String tag, @Nullable Player deleter){
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()) return;
        ResultSet results = getSingleGroupResult(tag);
        Connection connection = null;
        Statement statement = null;
        try{
            if (results == null || !results.next()){
                if (deleter != null){
                    deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Entity Group does not exist in MYSQL database!");
                }
                return;
            }
            connection = getConnection();
            statement =  connection.createStatement();
            String delete = "DELETE FROM saved_displays WHERE tag = \""+tag+"\";";
            statement.executeUpdate(delete);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted from MYSQL"));
            }
        }
        catch(SQLException ignored){

        }
        finally {
            DbUtils.closeQuietly(results);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

    }

    static DisplayEntityGroup retrieveDisplayEntityGroup(String tag){
        if (!isConnected()){
            return null;
        }
        ResultSet results = getSingleGroupResult(tag);
        try{
            if (results == null || !results.next()){
                return null;
            }
            Blob blob = results.getBlob("display_group");
            return DisplayGroupManager.getGroup(blob.getBinaryStream());
        }
        catch(SQLException e){
            return null;
        }
        finally {
            DbUtils.closeQuietly(results);
        }
    }

    static boolean saveDisplayAnimation(DisplayAnimation displayAnimation, @Nullable Player saver){
        if (!connected){
            return false;
        }
        PreparedStatement statement = null;
        Connection connection = null;
        try{
            String tag = displayAnimation.getAnimationTag();
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(displayAnimation);
            gzipOut.close();
            objOut.close();

            byte[] data = byteOut.toByteArray();
            byteOut.close();
            ByteArrayInputStream blobStream = new ByteArrayInputStream(data);

            String save = "INSERT INTO saved_animations VALUES(\""+tag+"\", ?);";
            connection = getConnection();
            statement = connection.prepareStatement(save);
            statement.setBlob(1, blobStream);
            if (retrieveDisplayAnimation(tag) != null){
                if (DisplayEntityPlugin.overwritexistingSaves()){
                    deleteDisplayEntityGroup(tag, null);
                }
                else{
                    if (saver != null) {
                        saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display animation to MYSQL!");
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }
            }
            statement.executeUpdate();
            blobStream.close();
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- "+ ChatColor.GREEN + "Successfully saved display animation to MYSQL!");
            }
            return true;
        }
        catch(SQLException | IOException e){
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display animation to MYSQL!");
            }
            e.printStackTrace();
            return false;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    static void deleteDisplayAnimation(String tag, @Nullable Player deleter){
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()){
            return;
        }
        Statement statement = null;
        Connection connection = null;
        ResultSet results = getSingleAnimationResult(tag);
        try{
            if (results == null || !results.next()){
                if (deleter != null){
                    deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Animation does not exist in MYSQL database!");
                }
                return;
            }
            connection = getConnection();
            statement = getConnection().createStatement();
            String delete = "DELETE FROM saved_animations WHERE tag = \""+tag+"\";";
            statement.executeUpdate(delete);
            if (deleter != null){
                deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.LIGHT_PURPLE+"Successfully deleted from MYSQL!");
            }
        }
        catch(SQLException ignored){
        }

        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(results);
            DbUtils.closeQuietly(connection);
        }

    }

    static DisplayAnimation retrieveDisplayAnimation(String tag) {
        if (!isConnected()){
            return null;
        }
        ResultSet results = getSingleAnimationResult(tag);
        try {
            if (results == null || !results.next()){
                return null;
            }
            Blob blob = results.getBlob("display_anim");
            return DisplayAnimationManager.getAnimation(blob.getBinaryStream());
        } catch (SQLException e) {
            return null;
        }
        finally {
            DbUtils.closeQuietly(results);
        }
    }

    static List<String> getDisplayEntityTags(){
        List<String> tags = new ArrayList<>();
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()) return tags;
        ResultSet results = getAllGroupResults();
        try{
            if (results == null){
                return tags;
            }
            while(results.next()){
                tags.add(results.getString("tag"));
            }
            return tags;
        }
        catch(SQLException e){
            return tags;
        }
        finally {
            DbUtils.closeQuietly(results);
        }
    }

    static List<String> getDisplayAnimationTags(){
        List<String> tags = new ArrayList<>();
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()) return tags;
        ResultSet results = getAllAnimationResults();
        try{
            if (results == null){
                return tags;
            }
            while(results.next()){
                tags.add(results.getString("tag"));
            }
            return tags;
        }
        catch(SQLException e){
            return tags;
        }
        finally {
            DbUtils.closeQuietly(results);
        }
    }

    private static ResultSet getSingleGroupResult(String tag){
        Statement statement = null;
        Connection connection = null;
        try{
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_displays WHERE tag = \""+tag+"\";";
            return statement.executeQuery(retrieve);
        }catch(SQLException e){
            return null;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    private static ResultSet getAllGroupResults(){
        Statement statement = null;
        Connection connection = null;
        try{
            connection = getConnection();
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_displays;";
            return statement.executeQuery(retrieve);
        }catch(SQLException e){
            return null;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    private static ResultSet getSingleAnimationResult(String tag){
        Statement statement = null;
        Connection connection = null;
        try{
            connection = getConnection();
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_animations WHERE tag = \""+tag+"\";";
            return statement.executeQuery(retrieve);
        }catch(SQLException e){
            return null;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    private static ResultSet getAllAnimationResults(){
        Statement statement = null;
        Connection connection = null;
        try{
            connection = getConnection();
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_animations;";
            return statement.executeQuery(retrieve);
        }catch(SQLException e){
            return null;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }
}
