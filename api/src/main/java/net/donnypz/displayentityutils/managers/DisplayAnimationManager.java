package net.donnypz.displayentityutils.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public final class DisplayAnimationManager {

    private static Cache<String, SpawnedDisplayAnimation> cachedAnimations;
    private static final Gson gson = new Gson();

    private DisplayAnimationManager(){}

    @ApiStatus.Internal
    public static void createExpirationMap(int expireTime){
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (expireTime > -1){
            builder.expireAfterAccess(Duration.ofSeconds(expireTime));
        }
        cachedAnimations = builder.build();
    }

    /**
     * Set the selected SpawnedDisplayAnimation of a player to the specified animation
     * @param player Player to set the selection to
     * @param spawnedDisplayAnimation the animation the player should have selected
     */
    public static void setSelectedSpawnedAnimation(@NotNull Player player, @NotNull SpawnedDisplayAnimation spawnedDisplayAnimation){
        DEUUser.getOrCreateUser(player).setSelectedSpawnedAnimation(spawnedDisplayAnimation);
    }

    /**
     * Get the SpawnedDisplayAnimation a player has selected
     * @param player Player to get the animation of
     * @return The SpawnedDisplayAnimation that the player has selected. Null if player does not have a selection.
     */
    public static @Nullable SpawnedDisplayAnimation getSelectedSpawnedAnimation(@NotNull Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user != null) return user.getSelectedAnimation();
        return null;
    }

    /**
     * Remove a player's SpawnedDisplayAnimation selection
     * @param player Player to remove selection from
     */
    public static void deselectSpawnedAnimation(@NotNull Player player){
        DEUUser user = DEUUser.getUser(player);
        if (user != null) user.deselectSpawnedAnimation();
    }

    /**
     * Save a {@link DisplayAnimation} to a specified storage location
     * @param loadMethod where to save the animation
     * @param displayAnimation the animation to be saved
     * @param saver player who is saving the animation
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayAnimation(@NotNull LoadMethod loadMethod, @NotNull DisplayAnimation displayAnimation, @Nullable Player saver){
        if (displayAnimation.getAnimationTag() == null || !loadMethod.isEnabled()){
            return false;
        }
        boolean success = DisplayAPI.getStorage(loadMethod).saveDisplayAnimation(displayAnimation, saver);
        if (success){
           attemptCacheAnimation(displayAnimation.getAnimationTag(), displayAnimation.toSpawnedDisplayAnimation());
        }
        return success;
    }

    /**
     * Save a {@link DisplayAnimation} to a local storage as a json file
     * @param displayAnimation the animation to be saved
     * @param saver player who is saving the animation
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayAnimationJson(@NotNull DisplayAnimation displayAnimation, @Nullable Player saver){
        try{
            File saveFile = new File(PluginFolders.animSaveFolder, "/"+displayAnimation.getAnimationTag()+".json");
            if (saveFile.exists()){
                if (!DisplayConfig.overwritexistingSaves()){
                    if (saver != null){
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save animation <light_purple>JSON <red>locally!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }
                saveFile.delete();
            }
            saveFile.createNewFile();

            FileWriter fileWriter = new FileWriter(saveFile);

            JsonElement jsonEl = gson.toJsonTree(displayAnimation);
            JsonObject jsonObj = jsonEl.getAsJsonObject();
            DisplayGroupManager.serializeJsonElement(jsonObj);
            jsonObj.addProperty(DisplayGroupManager.PLUGIN_VERSION_FIELD, DisplayAPI.getVersion());

            fileWriter.write(gson.toJson(jsonObj));
            fileWriter.close();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved animation <light_purple>JSON <green>locally!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save animation <light_purple>JSON <red>locally!"));
            }
            return false;
        }
    }

    /**
     * Delete a {@link DisplayAnimation} from a storage location
     * @param loadMethod where the DisplayAnimation is located
     * @param tag tag of the animation to be deleted
     * @param deleter player who is deleting the animation
     */
    public static void deleteDisplayAnimation(@NotNull LoadMethod loadMethod, @NotNull String tag, @Nullable Player deleter) {
        if (loadMethod.isEnabled()){
            DisplayAPI.getStorage(loadMethod).deleteDisplayAnimation(tag, deleter);
        }
    }

    /**
     * Get a cached {@link SpawnedDisplayAnimation}
     * @param animationTag the tag of the animation
     * @return a {@link SpawnedDisplayAnimation} or null if not cached
     */
    public static @Nullable SpawnedDisplayAnimation getCachedAnimation(@NotNull String animationTag){
        return cachedAnimations.getIfPresent(animationTag);
    }

    /**
     * Get a cached {@link SpawnedDisplayAnimation}. If the desired animation is not cached, it will be fetched with the specified {@link LoadMethod}
     * @param animationTag the tag of the animation
     * @param loadMethod The lhe storage location of the animation. Null if unknown or to search all locations.
     * <hr>
     * <p>If an animation is not already cached, an attempt will be made to retrieve from the desired storage location</p>
     * @return a {@link SpawnedDisplayAnimation}, or null if the animation is not cached and does not exist in any storage location.
     */
    public static @Nullable SpawnedDisplayAnimation getSpawnedDisplayAnimation(@NotNull String animationTag, @Nullable LoadMethod loadMethod){


        //Check Cache
        SpawnedDisplayAnimation spawnedAnim = getCachedAnimation(animationTag);
        if (spawnedAnim != null){
            return spawnedAnim.clone();
        }

    //Check storage
        DisplayAnimation anim = loadMethod == null ? getAnimation(animationTag) : getAnimation(loadMethod, animationTag);

        if (anim == null){
            return null;
        }

        spawnedAnim = anim.toSpawnedDisplayAnimation();
        attemptCacheAnimation(animationTag, spawnedAnim);
        return spawnedAnim;
    }

    private static void attemptCacheAnimation(String tag, SpawnedDisplayAnimation animation){
        if (DisplayConfig.cacheAnimations()){
            cachedAnimations.put(tag, animation);
        }
    }

    /**
     * Attempt to get a {@link DisplayAnimation} from all storage locations.
     * Storage locations in the order of {@link LoadMethod#LOCAL}, {@link LoadMethod#MONGODB}, {@link LoadMethod#MYSQL}.
     * If a load method is disabled then it is skipped.
     * @param tag The tag of the {@link DisplayAnimation} to be retrieved
     * @return The found {@link DisplayAnimation}. Null if not found.
     */
    public static @Nullable DisplayAnimation getAnimation(@NotNull String tag){
        DisplayAnimation anim = getAnimation(LoadMethod.LOCAL, tag);
        if (anim == null){
            anim = getAnimation(LoadMethod.MONGODB, tag);
        }
        if (anim == null){
            anim = getAnimation(LoadMethod.MYSQL, tag);
        }
        return anim;
    }


    /**
     * Get a {@link DisplayAnimation} from a storage location.
     * @param loadMethod Where the {@link DisplayAnimation} is located
     * @param tag The tag of the {@link DisplayAnimation} to be retrieved
     * @return The found {@link DisplayAnimation}. Null if not found.
     */
    public static @Nullable DisplayAnimation getAnimation(@NotNull LoadMethod loadMethod, @NotNull String tag){
        if (!loadMethod.isEnabled()) return null;
        return DisplayAPI.getStorage(loadMethod).getDisplayAnimation(tag);
    }

    /**
     * Get a {@link DisplayAnimation} from a saved file
     * @param file File of a saved {@link DisplayAnimation}
     * @return The found {@link DisplayAnimation}. Null if not found.
     */
    public static @Nullable DisplayAnimation getAnimation(@NotNull File file){
        try(FileInputStream stream = new FileInputStream(file)){
            return getAnimation(stream);
        }
        catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a {@link DisplayAnimation} from an input stream
     * @param inputStream InputStream containing a saved {@link DisplayAnimation}.
     * @return The found {@link DisplayAnimation}. Null if not found.
     */
    public static @Nullable DisplayAnimation getAnimation(@NotNull InputStream inputStream){
        byte[] bytes;
        try{
            bytes = inputStream.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try(ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteStream);
            ObjectInputStream objIn = new DisplayAnimationInputStream(gzipInputStream)
        ){

            DisplayAnimation anim = (DisplayAnimation) objIn.readObject();
            anim.adaptOldSounds();
            PartFilter filter = anim.getPartFilter();
            if (filter != null) filter.deserializeMaterials();

            for (DisplayAnimationFrame f : anim.getFrames()){
                for (AnimationParticle p : f.getFrameStartParticles()){
                    p.initializeParticle();
                }
                for (AnimationParticle p : f.getFrameEndParticles()){
                    p.initializeParticle();
                }
                for (FramePoint p : f.getFramePoints()){
                    p.initialize();
                }
            }
            return anim;
        }

    //Not Compressed (Will be an older file version, before gzip compression)
        catch (ZipException z){
            try(ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
                ObjectInputStream objIn = new DisplayAnimationInputStream(byteStream)
            ){
                DisplayAnimation anim = (DisplayAnimation) objIn.readObject();
                anim.adaptOldSounds();
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
     * Get a {@link DisplayAnimation} from a plugin's resources
     * @param plugin The plugin to get the animation from
     * @param resourcePath The path of the animation
     * @return The found {@link DisplayAnimation}. Null if not found.
     */
    public static @Nullable DisplayAnimation getAnimation(@NotNull JavaPlugin plugin, @NotNull String resourcePath){
        try(InputStream stream = plugin.getResource(resourcePath)){
            if (stream == null) return null;
            return getAnimation(stream);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the animation tags of all saved {@link DisplayAnimation}s in a storage location.
     * @param loadMethod of the search location
     * @return a list of all animations by their group tag
     */
    public static @NotNull List<String> getSavedDisplayAnimations(@NotNull LoadMethod loadMethod){
        if (!loadMethod.isEnabled()) return Collections.emptyList();
        return DisplayAPI.getStorage(loadMethod).getAnimationTags();
    }

    /**
     * Get a {@link DisplayAnimation} from a JSON string
     * @param json JSON of a saved {@link DisplayAnimation}
     * @return The represented {@link DisplayAnimation} or null.
     */
    public static @Nullable DisplayAnimation getAnimationFromJSON(@NotNull String json){
        return getAnimationFromJSON(JsonParser.parseString(json).getAsJsonObject());
    }


    /**
     * Get a {@link DisplayAnimation} from a saved file, containing JSON data
     * @param jsonFile JSON file of a saved {@link DisplayAnimation}
     * @return The found {@link DisplayAnimation}. Null if not found.
     */
    public static @Nullable DisplayAnimation getAnimationFromJSON(@NotNull File jsonFile){
        try{
            String json = Files.readString(jsonFile.toPath());
            return getAnimationFromJSON(JsonParser.parseString(json).getAsJsonObject());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a {@link DisplayAnimation} from an input stream, containing JSON data
     * @param inputStream InputStream containing a saved {@link DisplayAnimation}.
     * @return The found {@link DisplayAnimation} or null.
     */
    public static @Nullable DisplayAnimation getAnimationFromJSON(@NotNull InputStream inputStream){
        try{
            return getAnimationFromJSON(JsonParser
                    .parseString(new String(inputStream.readAllBytes()))
                    .getAsJsonObject());
        }
        catch(IOException ex){
            return null;
        }
    }

    /**
     * Get a {@link DisplayAnimation} from a plugin's resources, which is stored as a JSON file
     * @param plugin The plugin to get the animation from
     * @param resourcePath The path of the animation
     * @return The found {@link DisplayAnimation}. Null if not found.
     */
    public static @Nullable DisplayAnimation getAnimationFromJSON(@NotNull JavaPlugin plugin, @NotNull String resourcePath){
        try(InputStream stream = plugin.getResource(resourcePath)){
            if (stream == null) return null;
            return getAnimationFromJSON(stream);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static DisplayAnimation getAnimationFromJSON(JsonObject jsonObject){
        DisplayGroupManager.deserializeJsonElement(jsonObject);
        jsonObject.remove(DisplayGroupManager.PLUGIN_VERSION_FIELD);

        return DEGJSONAdapter
                .GSON
                .fromJson(jsonObject, DisplayAnimation.class);
    }
}
