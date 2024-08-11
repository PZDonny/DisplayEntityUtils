package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public final class DisplayAnimationManager {

    private DisplayAnimationManager(){}
    private static final HashMap<UUID, SpawnedDisplayAnimation> selectedAnimation = new HashMap<>();

    /**
     * Set the selected SpawnedDisplayAnimation of a player to the specified group
     * @param player Player to set the selection to
     * @param spawnedDisplayAnimation SpawnedDisplayAnimation to be set to the player
     */
    public static void setSelectedSpawnedAnimation(Player player, SpawnedDisplayAnimation spawnedDisplayAnimation){
        if (selectedAnimation.get(player.getUniqueId()) != null){
            SpawnedDisplayAnimation lastAnim = selectedAnimation.get(player.getUniqueId());
            if (lastAnim == spawnedDisplayAnimation){
                return;
            }

            boolean otherPlayersHaveSelected = false;
            for (UUID uuid : selectedAnimation.keySet()){
                if (uuid != player.getUniqueId() && selectedAnimation.get(uuid) == lastAnim){
                    otherPlayersHaveSelected = true;
                    break;
                }
            }

            if (!otherPlayersHaveSelected){
                lastAnim.remove();
            }
        }
        selectedAnimation.put(player.getUniqueId(), spawnedDisplayAnimation);
    }

    /**
     * Gets the SpawnedDisplayAnimation a player has selected
     * @param player Player to get the animation of
     * @return The SpawnedDisplayAnimation that the player has selected. Null if player does not have a selection.
     */
    public static SpawnedDisplayAnimation getSelectedSpawnedAnimation(Player player) {
        return selectedAnimation.get(player.getUniqueId());
    }

    /**
     * Remove a player's SpawnedDisplayAnimation selection
     * @param player Player to remove selection from
     */
    public static void deselectSpawnedAnimation(Player player){
        selectedAnimation.remove(player.getUniqueId());
    }


    /**
     * Save a DisplayAnimation to a Data Location
     * @param loadMethod Storage where to save the DisplayAnimation
     * @param displayAnimation The group to be saved
     * @param saver Player who is saving the animation (Nullable)
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayAnimation(LoadMethod loadMethod, DisplayAnimation displayAnimation, @Nullable Player saver){
        if (displayAnimation.getAnimationTag() == null){
            return false;
        }
        switch(loadMethod){
            case LOCAL -> {
                if (DisplayEntityPlugin.isLocalEnabled()){
                    return LocalManager.saveDisplayAnimation(displayAnimation, saver);
                }
            }
            case MONGODB -> {
                if (DisplayEntityPlugin.isMongoEnabled()){
                    return MongoManager.saveDisplayAnimation(displayAnimation, saver);
                }
            }
            case MYSQL -> {
                if (DisplayEntityPlugin.isMYSQLEnabled()){
                    return MYSQLManager.saveDisplayAnimation(displayAnimation, saver);
                }
            }
        }
        return false;
    }

    /**
     * Delete a DisplayAnimation from a Data Location
     * @param loadMethod Storage where the DisplayAnimation is located
     * @param tag Tag of the animation to be deleted
     * @param deleter Player who is deleting the animation (Nullable)
     */
    public static void deleteDisplayAnimation(LoadMethod loadMethod, String tag, @Nullable Player deleter){
        switch(loadMethod){
            case LOCAL -> {
                LocalManager.deleteDisplayAnimation(tag, deleter);
            }
            case MONGODB ->{
                MongoManager.deleteDisplayAnimation(tag, deleter);
            }
            case MYSQL -> {
                MYSQLManager.deleteDisplayAnimation(tag, deleter);
            }
        }
    }


    /**
     * Get a Display Entity Group from a Data Location
     * @param loadMethod Where the DisplayAnimation is located
     * @param tag The tag of the DisplayAnimation to be retrieved
     * @return The found DisplayAnimation. Null if not found.
     */
    public static DisplayAnimation retrieve(LoadMethod loadMethod, String tag){
        switch(loadMethod){
            case LOCAL ->{
                return LocalManager.retrieveDisplayAnimation(tag);
            }
            case MONGODB -> {
                return MongoManager.retrieveDisplayAnimation(tag);
            }
            case MYSQL -> {
                return MYSQLManager.retrieveDisplayAnimation(tag);
            }
        }
        return null;
    }

    /**
     * Get a DisplayAnimation from a saved file
     * @param file File of a saved DisplayAnimation
     * @return The found DisplayAnimation. Null if not found.
     */
    public static DisplayAnimation retrieve(File file){
        try{
            return retrieve(new FileInputStream(file));
        }
        catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a Display Entity Group from an input stream
     * @param inputStream InputStream containing a saved DisplayAnimation
     * @return The found DisplayAnimation. Null if not found.
     */
    public static DisplayAnimation retrieve(InputStream inputStream){
        byte[] bytes;
        try{
            bytes = inputStream.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try{
            ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteStream);

            ObjectInputStream objIn = new DisplayObjectInputStream(gzipInputStream);
            DisplayAnimation anim = (DisplayAnimation) objIn.readObject();

            objIn.close();
            gzipInputStream.close();
            byteStream.close();
            inputStream.close();
            return anim;
        }
    //Not Compressed (Will typically be old file version)
        catch (ZipException z){
            try{
                ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
                ObjectInputStream objIn = new DisplayObjectInputStream(byteStream);
                DisplayAnimation anim = (DisplayAnimation) objIn.readObject();

                objIn.close();
                byteStream.close();
                inputStream.close();
                return anim;
            }
            catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        catch(IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a DisplayAnimation from a plugin's resources
     * @param plugin The plugin to get the DisplayAnimation from
     * @param resourcePath The path of the DisplayAnimation
     * @return The found DisplayAnimation. Null if not found.
     */
    public static DisplayAnimation retrieveFromResources(JavaPlugin plugin, String resourcePath){
        InputStream modelStream;
        if (resourcePath.contains(DisplayAnimation.fileExtension)){
            modelStream = plugin.getResource(resourcePath);
        }
        else{
            modelStream = plugin.getResource(resourcePath+DisplayAnimation.fileExtension);
        }
        return retrieve(modelStream);
    }


    /**
     * Gets a String List of the tag of display animations in a storage location
     * @param loadMethod of the search location
     * @return String list of the tags of saved display animations in the specified storage location. Returns an empty list if nothing was found
     */
    public static List<String> getDisplayAnimationTags(@Nonnull LoadMethod loadMethod){
        switch(loadMethod){
            case LOCAL -> {
                return LocalManager.getDisplayAnimationTags();
            }
            case MONGODB -> {
                return MongoManager.getDisplayAnimationTags();
            }
            case MYSQL -> {
                return MYSQLManager.getDisplayAnimationTags();
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }
    
}
