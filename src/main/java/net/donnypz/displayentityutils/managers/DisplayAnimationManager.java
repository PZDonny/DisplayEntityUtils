package net.donnypz.displayentityutils.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public final class DisplayAnimationManager {

    private DisplayAnimationManager(){}
    private static Cache<String, SpawnedDisplayAnimation> cachedAnimations;

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
     * Save a DisplayAnimation to a Data Location
     * @param loadMethod Storage where to save the DisplayAnimation
     * @param displayAnimation The group to be saved
     * @param saver Player who is saving the animation
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayAnimation(@NotNull LoadMethod loadMethod, @NotNull DisplayAnimation displayAnimation, @Nullable Player saver){
        if (displayAnimation.getAnimationTag() == null){
            return false;
        }
        boolean success = false;
        switch(loadMethod){
            case LOCAL -> {
                if (DisplayEntityPlugin.isLocalEnabled()){
                    success = LocalManager.saveDisplayAnimation(displayAnimation, saver);
                }
            }
            case MONGODB -> {
                if (DisplayEntityPlugin.isMongoEnabled()){
                    success = MongoManager.saveDisplayAnimation(displayAnimation, saver);
                }
            }
            case MYSQL -> {
                if (DisplayEntityPlugin.isMYSQLEnabled()){
                    success = MYSQLManager.saveDisplayAnimation(displayAnimation, saver);
                }
            }
        }
        if (success){
           attemptCacheAnimation(displayAnimation.getAnimationTag(), displayAnimation.toSpawnedDisplayAnimation());
        }
        return success;
    }

    /**
     * Delete a DisplayAnimation from a Data Location
     * @param loadMethod Storage where the DisplayAnimation is located
     * @param tag Tag of the animation to be deleted
     * @param deleter Player who is deleting the animation (Nullable)
     */
    public static void deleteDisplayAnimation(@NotNull LoadMethod loadMethod, @NotNull String tag, @Nullable Player deleter) {
        switch (loadMethod) {
            case LOCAL -> {
                LocalManager.deleteDisplayAnimation(tag, deleter);
            }
            case MONGODB -> {
                MongoManager.deleteDisplayAnimation(tag, deleter);
            }
            case MYSQL -> {
                MYSQLManager.deleteDisplayAnimation(tag, deleter);
            }
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
        DisplayAnimation anim;
        if (loadMethod == null){
            anim = getAnimation(animationTag);
        }
        else{
            anim = getAnimation(loadMethod, animationTag);
        }

        if (anim == null){
            return null;
        }

        spawnedAnim = anim.toSpawnedDisplayAnimation();
        attemptCacheAnimation(animationTag, spawnedAnim);
        return spawnedAnim;
    }

    private static void attemptCacheAnimation(String tag, SpawnedDisplayAnimation animation){
        if (DisplayEntityPlugin.cacheAnimations()){
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
    public static List<String> getSavedDisplayAnimations(@NotNull LoadMethod loadMethod){
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
