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
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;


public final class MYSQLManager {

    private static boolean connected = false;
    private static HikariDataSource dataSource;

    private MYSQLManager(){}

    @ApiStatus.Internal
    public static void createConnection(String host, int port, String database, String username, String password, boolean usessl){
        if (connected){
            return;
        }
        String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true&allowMultiQueries=true&useSSL="+usessl;
        createConnection(url, username, password);
    }

    @ApiStatus.Internal
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

                Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<aqua>Successfully connected to <blue>MYSQL!")));
                connected = true;
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("There was an error connecting to the MYSQL database", NamedTextColor.RED)));
                closeConnection();
            }
        });
    }

    @ApiStatus.Internal
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
     * @return a boolean
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
        catch(SQLIntegrityConstraintViolationException e){
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group to MYSQL. That group already exists!");
            }
            e.printStackTrace();
            return false;
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
        Connection connection = null;
        Statement statement = null;
        try{
            connection = getConnection();

            if (!hasSingleGroup(tag, connection)){
                if (deleter != null){
                    deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Entity Group does not exist in MYSQL database!");
                }
                return;
            }

            statement =  connection.createStatement();
            String delete = "DELETE FROM saved_displays WHERE tag = \""+tag+"\";";
            statement.executeUpdate(delete);
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted from MYSQL"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    static DisplayEntityGroup retrieveDisplayEntityGroup(String tag){
        if (!isConnected()){
            return null;
        }
        Blob blob = getSingleGroupBlob(tag);
        if (blob == null) return null;
        try{
            return DisplayGroupManager.getGroup(blob.getBinaryStream());
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
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
                    deleteDisplayAnimation(tag, null);
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
        ResultSet resultSet = null;
        try{
            connection = getConnection();

            if (!hasSingleAnimation(tag, connection)){
                if (deleter != null){
                    deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Animation does not exist in MYSQL database!");
                }
                return;
            }

            statement = connection.createStatement();
            String delete = "DELETE FROM saved_animations WHERE tag = \""+tag+"\";";
            statement.executeUpdate(delete);
            if (deleter != null){
                deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.LIGHT_PURPLE+"Successfully deleted from MYSQL!");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    static DisplayAnimation retrieveDisplayAnimation(String tag) {
        if (!isConnected()){
            return null;
        }
        Blob blob = getSingleAnimationBlob(tag);
        if (blob == null) return null;
        try {
            return DisplayAnimationManager.getAnimation(blob.getBinaryStream());
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static List<String> getDisplayEntityTags(){
        List<String> tags = new ArrayList<>();
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()) return tags;
        return getTags("saved_displays");
    }

    static List<String> getDisplayAnimationTags(){
        if (!DisplayEntityPlugin.isMYSQLEnabled() || !isConnected()) return Collections.emptyList();
        return getTags("saved_animations");
    }

    private static boolean hasSingleGroup(String tag, Connection connection){
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_displays WHERE tag = \""+tag+"\";";
            resultSet = statement.executeQuery(retrieve);
            return resultSet.next();
        }catch(SQLException e){
            return false;
        }
        finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
        }
    }

    private static Blob getSingleGroupBlob(String tag){
        Statement statement = null;
        Connection connection = null;
        try{
            connection = getConnection();
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_displays WHERE tag = \""+tag+"\";";
            ResultSet results = statement.executeQuery(retrieve);
            if (results != null && results.next()){
                return results.getBlob("display_group");
            }
            else{
                return null;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    private static boolean hasSingleAnimation(String tag, Connection connection){
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_animations WHERE tag = \""+tag+"\";";
            resultSet = statement.executeQuery(retrieve);
            return resultSet.next();
        }catch(SQLException e){
            return false;
        }
        finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
        }
    }

    private static Blob getSingleAnimationBlob(String tag){
        Statement statement = null;
        Connection connection = null;
        try{
            connection = getConnection();
            statement = connection.createStatement();
            String retrieve = "SELECT * FROM saved_animations WHERE tag = \""+tag+"\";";
            ResultSet results = statement.executeQuery(retrieve);
            if (results != null && results.next()){
                return results.getBlob("display_anim");
            }
            else{
                return null;
            }
        }catch(SQLException e){
            return null;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    private static List<String> getTags(String tableName){
        List<String> tags = new ArrayList<>();
        String retrieve = "SELECT * FROM "+tableName+";";
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(retrieve)){

            while(results.next()){
                tags.add(results.getString("tag"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return tags;
    }
}
