package net.donnypz.displayentityutils.database;

import com.zaxxer.hikari.HikariDataSource;
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
import org.apache.commons.dbutils.DbUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class MYSQLDisplayStorage implements DisplayStorage {

    private static final String GROUP_TABLE = "saved_displays";
    private static final String GROUP_COLUMN = "display_group";
    private static final String GROUP_DISPLAY_NAME = "display entity group";

    private static final String ANIMATION_TABLE = "saved_animations";
    private static final String ANIMATION_COLUMN = "display_anim";
    private static final String ANIMATION_DISPLAY_NAME = "animation";

    private static final String TAG_COLUMN = "tag";


    private boolean connected = false;
    private HikariDataSource dataSource;

    public void createConnection(
            String host,
            int port,
            String database,
            String username,
            String password,
            boolean usessl
    ){
        if (connected){
            return;
        }
        String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true&allowMultiQueries=true&useSSL="+usessl;
        createConnection(url, username, password);
    }

    public void createConnection(
            String url,
            String username,
            String password
    ){
        if (connected){
            return;
        }
        DisplayAPI.getScheduler().runAsync(() -> {
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
                String groupTableSQL = "CREATE TABLE IF NOT EXISTS saved_displays(tag VARCHAR(128) UNIQUE, display_group BLOB)";
                statement.execute(groupTableSQL);

                String animTableSQL = "CREATE TABLE IF NOT EXISTS saved_animations(tag VARCHAR(128) UNIQUE, display_anim BLOB)";
                statement.execute(animTableSQL);


                DbUtils.closeQuietly(statement);
                DbUtils.closeQuietly(connection);

                Bukkit.getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<aqua>Successfully connected to <blue>MYSQL!")));
                connected = true;
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(Component.text("There was an error connecting to the MYSQL database", NamedTextColor.RED)));
                closeConnection();
            }
        });
    }

    public void closeConnection(){
        try{
            if (dataSource != null){
                dataSource.close();
            }
        }
        finally {
            connected = false;
            dataSource = null;
        }
    }

    /**
     * Check whether MySQL is connected
     * @return a boolean
     */
    public boolean isConnected() {
        return connected;
    }

    private Connection getConnection(){
        try{
            return dataSource.getConnection();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean saveDisplayEntityGroup(@NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        String tag = displayEntityGroup.getTag();
        return saveEntity(tag, displayEntityGroup, GROUP_TABLE, GROUP_DISPLAY_NAME, saver);
    }

    @Override
    public void deleteDisplayEntityGroup(@NotNull String tag, @Nullable Player deleter){
        deleteEntity(tag, deleter, GROUP_TABLE, "display entity group");
    }

    @Override
    public @Nullable DisplayEntityGroup getDisplayEntityGroup(@NotNull String tag){
        if (!isConnected()){
            return null;
        }
        Blob blob = getEntity(tag, GROUP_TABLE, GROUP_COLUMN);
        if (blob == null) return null;
        try{
            return DisplayGroupManager.getGroup(blob.getBinaryStream());
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean saveDisplayAnimation(@NotNull DisplayAnimation displayAnimation, @Nullable Player saver){
        String tag = displayAnimation.getAnimationTag();
        return saveEntity(tag, displayAnimation, ANIMATION_TABLE, ANIMATION_DISPLAY_NAME, saver);
    }

    @Override
    public void deleteDisplayAnimation(@NotNull String tag, @Nullable Player deleter){
        deleteEntity(tag, deleter, ANIMATION_TABLE, "animation");
    }

    @Override
    public @Nullable DisplayAnimation getDisplayAnimation(@NotNull String tag) {
        if (!isConnected()) return null;

        try {
            Blob blob = getEntity(tag, ANIMATION_TABLE, ANIMATION_COLUMN);
            return blob == null
                    ? null
                    : DisplayAnimationManager.getAnimation(blob.getBinaryStream());
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @NotNull List<String> getGroupTags(){
        return getTags(GROUP_TABLE);
    }

    @Override
    public @NotNull List<String> getAnimationTags(){
        return getTags(ANIMATION_TABLE);
    }

    private boolean saveEntity(String tag, Object entity, String tableName, String displayName, Player saver){
        if (!isConnected()) return false;
        String save = "INSERT INTO "+tableName+" VALUES(?, ?);";

        try(
                ByteArrayInputStream blobStream = CommonDisplayStorageUtils.toByteArrayInputStream(entity);
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(save);
        ){

            statement.setString(1, tag);
            statement.setBlob(2, blobStream);

            if (hasEntity(tag, tableName, connection)){
                if (DisplayConfig.overwritexistingSaves()){
                    deleteDisplayAnimation(tag, null);
                }
                else{
                    if (saver != null) {
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save "+displayName+" to MYSQL!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }
            }
            statement.executeUpdate();
            blobStream.close();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved "+displayName+" to MYSQL!"));
            }
            return true;
        }
        catch(SQLIntegrityConstraintViolationException e){
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save "+displayName+" to MYSQL!"));
                saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
            }
            e.printStackTrace();
            return false;
        }
        catch(SQLException | IOException e){
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save "+displayName+" to MYSQL!"));
            }
            e.printStackTrace();
            return false;
        }
    }



    private void deleteEntity(String tag, Player deleter, String tableName, String displayName){
        if (!isConnected()) return;

        String delete = "DELETE FROM "+tableName+" WHERE "+TAG_COLUMN+" = ?;";
        try(
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(delete);
        ){

            if (!hasEntity(tag, tableName, connection)){
                if (deleter != null){
                    deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved "+displayName+" does not exist in MYSQL database!"));
                }
                return;
            }

            statement.setString(1, tag);
            statement.executeUpdate(delete);
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted "+displayName+" from MYSQL database!"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved "+displayName+" does not exist in MYSQL database!"));
        }
    }

    private Blob getEntity(String tag, String tableName, String columnName){
        PreparedStatement statement = null;
        Connection connection = null;
        try{
            connection = getConnection();
            String retrieve = "SELECT "+columnName+" FROM "+tableName+" WHERE "+TAG_COLUMN+" = ?;";
            statement = connection.prepareStatement(retrieve);
            statement.setString(1, tag);

            ResultSet results = statement.executeQuery();

            return results.next()
                    ? results.getBlob(columnName)
                    : null;
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    private boolean hasEntity(String tag, String tableName, Connection connection){
        String retrieve = "SELECT 1 FROM "+tableName+" WHERE "+TAG_COLUMN+" = ?;";

        try(PreparedStatement statement = connection.prepareStatement(retrieve)){
            statement.setString(1, tag);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
        catch(SQLException e){
            return false;
        }
    }

    private List<String> getTags(String tableName){ //internally set table name
        if (!isConnected()) return Collections.emptyList();
        List<String> tags = new ArrayList<>();
        String retrieve = "SELECT "+TAG_COLUMN+" FROM "+tableName+";";

        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(retrieve)){

            while(results.next()){
                tags.add(results.getString(TAG_COLUMN));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return tags;
    }
}
