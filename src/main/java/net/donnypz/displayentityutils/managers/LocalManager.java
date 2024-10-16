package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.listeners.bdengine.DEUEntitySpawned;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class LocalManager {
    static File groupSaveFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/savedentities/");
    static File animSaveFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/animations/");
    static File animDatapackFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/bdenginedatapacks/");
    public static final String datapackConvertDeleteSubParentTag = "deu_delete_sub_parent";
    public static final String datapackUngroupedAddLaterTag = "deu_add_later";
    private static final CommandSender silentSender = Bukkit.createCommandSender(feedback -> {});

    private LocalManager(){}

    static boolean saveDisplayEntityGroup(DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        if (!DisplayEntityPlugin.isLocalEnabled()){
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

            File saveFile = new File(groupSaveFolder, "/"+displayEntityGroup.getTag()+DisplayEntityGroup.fileExtension);
            if (saveFile.exists()){
                if (DisplayEntityPlugin.overwritexistingSaves()){
                    saveFile.delete();
                }
                else{
                    if (saver != null){
                        saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group locally!");
                        saver.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Save with tag already exists!");
                    }
                    return false;
                }

            }
            saveFile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            fileOut.write(data);
            fileOut.close();
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- "+ ChatColor.GREEN + "Successfully saved display entity group locally!");
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display entity group locally!");
            }
            return false;
        }
    }

    static void deleteDisplayEntityGroup(String tag, @Nullable Player deleter){
        if (!DisplayEntityPlugin.isLocalEnabled()) return;
        File saveFile = new File(groupSaveFolder, "/"+tag+DisplayEntityGroup.fileExtension);
        if (saveFile.exists()){
            saveFile.delete();
            if (deleter != null){
                deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.LIGHT_PURPLE+"Successfully deleted from local files!");
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Entity Group does not exist in local files!");
        }
    }

    static DisplayEntityGroup retrieveDisplayEntityGroup(String tag){
        if (!DisplayEntityPlugin.isLocalEnabled()){
            return null;
        }
        File saveFile = new File(groupSaveFolder, "/"+tag+DisplayEntityGroup.fileExtension);
        if (!saveFile.exists()){
            return null;
        }
        return DisplayGroupManager.getGroup(saveFile);
    }

    static boolean saveDisplayAnimation(DisplayAnimation displayAnimation, @Nullable Player saver){
        if (!DisplayEntityPlugin.isLocalEnabled()){
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
            byteOut.close();

            File saveFile = new File(animSaveFolder, "/"+displayAnimation.getAnimationTag()+DisplayAnimation.fileExtension);
            if (saveFile.exists()){
                if (DisplayEntityPlugin.overwritexistingSaves()){
                    saveFile.delete();
                }
                else{
                    if (saver != null){
                        saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display animation locally!");
                        saver.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Save with tag already exists!");
                    }
                    return false;
                }

            }
            saveFile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            fileOut.write(data);
            fileOut.close();
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- "+ ChatColor.GREEN + "Successfully saved display animation locally!");
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(ChatColor.WHITE+"- " + ChatColor.RED + "Failed to save display animation locally!");
            }
            return false;
        }
    }

    public static void saveDatapackAnimation(Player player, String datapackName, @NotNull String groupSaveTag, @NotNull String animationSaveTag){
        if (!datapackName.endsWith(".zip")){
            datapackName = datapackName+".zip";
        }
        try{
            ZipFile zipFile = new ZipFile(LocalManager.getAnimationDatapackFolder()+"/"+datapackName);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            List<ZipEntry> frames = new ArrayList<>();

            long timestamp = 0;
            int totalGroups = 0;
            Location pLoc = player.getLocation();
            while (entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()){
                    continue;
                }
                if (entry.getName().contains("animation_keyframe")){
                    frames.add(entry);
                    continue;
                }
                if (entry.getName().contains("summon.mcfunction")){
                    timestamp = executeCommands(entry, zipFile, player, pLoc, true);
                }
                if (entry.getName().contains("start_animation.mcfunction")){
                    totalGroups = (int) executeCommands(entry, zipFile, player, pLoc, true);
                }
            }
            String finalDatapackName = datapackName;
            if (!frames.isEmpty() && timestamp > 0 && totalGroups > 0){
                int finalTotalGroups = totalGroups;
                long finalTimestamp = timestamp;
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    readAnimationFiles(finalTimestamp, finalTotalGroups, zipFile, frames, finalDatapackName, player, groupSaveTag, animationSaveTag);
                }, 30);
            }
        } catch (IOException e) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.RED+"Failed to find datapack with the provided name! Ensure that the datapack is placed in the \"bdenginedatapacks\" folder of this plugin, as a .zip file");
        }
    }

    private static void readAnimationFiles(long timeStamp, int totalGroups, ZipFile zipFile, List<ZipEntry> frames, String datapackName, @NotNull Player player, @NotNull String groupSaveTag, @NotNull String animationSaveTag){
        SpawnedDisplayEntityGroup createdGroup = DEUEntitySpawned.getTimestampGroup(timeStamp);
        if (createdGroup == null){
            throw new RuntimeException("Failed to fetch group from timestamp");
        }

        DEUEntitySpawned.finalizeTimestampedAnimationPreparation(timeStamp);
        createdGroup.seedPartUUIDs(SpawnedDisplayEntityGroup.defaultPartUUIDSeed);

        new BukkitRunnable(){
            final SpawnedDisplayAnimation anim = new SpawnedDisplayAnimation();
            int i = 0;
            @Override
            public void run() {
                if (i == frames.size()){
                    SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 2).setTransformation(createdGroup);
                    if (!frame.isEmptyFrame()){
                        anim.addFrame(frame);
                    }
                    createdGroup.setToFrame(anim, anim.getFrames().getFirst());

                    //Save with first frame applied to group
                    Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                        if (animationSaveTag.isBlank()){
                            anim.setAnimationTag(datapackName.replace(".zip", "")+"_autoconvert");
                        }
                        else{
                            anim.setAnimationTag(animationSaveTag);
                        }
                        boolean animationSuccess = DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim.toDisplayAnimation(), player);
                        anim.remove();

                        boolean groupSuccess;
                        if (!groupSaveTag.equals("-")){
                            if (groupSaveTag.isBlank()){
                                createdGroup.setTag(datapackName.replace(".zip", "")+"_autoconvert");
                            }
                            else{
                                createdGroup.setTag(groupSaveTag);
                            }
                            groupSuccess = DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.LOCAL, createdGroup.toDisplayEntityGroup(), player);
                        }
                        else{
                            groupSuccess = false;
                        }

                        createdGroup.unregister(true);

                        if (animationSuccess){
                            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.GREEN+"Successfully converted BDEngine animation to DisplayAnimation.");
                            player.sendMessage(Component.text(" | Animation Tag: "+anim.getAnimationTag(), NamedTextColor.GRAY));
                            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
                        }


                        if (groupSuccess){
                            player.sendMessage(Component.text(" | Group Tag: "+createdGroup.getTag(), NamedTextColor.GRAY));
                        }
                        else{
                            if (groupSaveTag.equals("-")){
                                player.sendMessage(Component.text("A group was not created during this conversion due to setting the group tag to \"-\"", NamedTextColor.GRAY));
                            }
                            else{
                                player.sendMessage(Component.text("An error occured when saving the group created from BDEngine conversion, refer to console and report errors if necessary", NamedTextColor.RED));
                            }
                        }

                        try{
                            zipFile.close();
                        }
                        catch(IOException e){
                            throw new RuntimeException("Failed to close zip file");
                        }
                    },1);

                    cancel();
                    return;
                }

                //Get Frame
                SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 2).setTransformation(createdGroup);
                if (!frame.isEmptyFrame()){
                    anim.addFrame(frame);
                }

                //Plays the command for the next frame for every group in the animation
                //If 2 groups exist, it will execute animation_keyframe0_1.mcfunction and animation_keyframe0_2.mcfunction
                //then animation_keyframe1_1.mcfunction and animation_keyframe1_2.mcfunction on the next iteration, and so on...
                Location pLoc = player.getLocation();
                for (int j = 0; j < totalGroups; j++){
                    ZipEntry entry = frames.get(i);
                    executeCommands(entry, zipFile, player, pLoc, false);
                    i++;
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 2, 2);
    }


    private static long executeCommands(ZipEntry zipEntry, ZipFile zipFile, Player player, Location location, boolean returnData){
        try{
            InputStream stream = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            long value = 0;
            while ((line = br.readLine()) != null){
                if (line.startsWith("#") || line.isEmpty()){
                    continue;
                }

                if (zipEntry.getName().contains("summon")){
                    String projectName = zipEntry.getName();
                    if (Bukkit.getUnsafe().getProtocolVersion() >= 767) { //1.21+
                        projectName = projectName.replace("function/summon.mcfunction", "");
                    }
                    //else{ //1.20.5+
                        projectName = projectName.replace("functions/summon.mcfunction", "");
                    //}

                    String[] splitDir = projectName.split("/");
                    projectName = splitDir[splitDir.length-1];

                //Master Part
                    if (line.contains("execute as @s")){

                        String coordinates = location.x()+" "+location.y()+" "+location.z();
                        String replacement = "execute at "+player.getName()+" run summon block_display "+coordinates;
                        line = line.replace("execute as @s run summon block_display ~ ~ ~", replacement);
                        String[] timestampSplit = line.split(":\\[\""+projectName.replace("_", ""));
                        try{
                            value = Long.parseLong(timestampSplit[1].replace("\"]}", ""));
                            DEUEntitySpawned.prepareAnimationMaster(value);
                        }
                        catch(NumberFormatException e){
                            br.close();
                            zipFile.close();
                            player.sendMessage(Component.text("Animation conversion failed! Read console", NamedTextColor.RED));
                            throw new RuntimeException("Failed to read command from zip entry: Invalid timestamp value");
                        }
                        catch(ArrayIndexOutOfBoundsException e){
                            br.close();
                            zipFile.close();
                            player.sendMessage(Component.text("Animation conversion failed! Read console", NamedTextColor.RED));
                            throw new RuntimeException("Failed to read command from zip entry: Wrong game version downloaded");
                        }
                    }
                //Sub-Master Parts
                    else if (line.contains("@s")){
                        //String coordinates = location.x()+" "+location.y()+" "+location.z();
                        //String replacement = "positioned "+coordinates;
                        String replacement = player.getName();
                        line = line.replace("@s", replacement);
                    }
                    line = line.substring(0, line.length()-2)+",\""+datapackConvertDeleteSubParentTag+"\"]}";
                }

                else if (zipEntry.getName().contains("start_animation.mcfunction")){
                    if (line.contains("function")){
                        value++;
                        continue;
                    }
                }
                else if (line.contains("function")) {
                    continue;
                }

                Bukkit.dispatchCommand(silentSender, line);

            }
            br.close();
            stream.close();

        //True if command was summon.mcfuntion command, returns group timestamp created by BDEngine
            if (returnData){
                return value;
            }
            else{
                return 0;
            }
        }
        catch (IOException e){
            player.sendMessage(Component.text("Animation conversion failed! Read console"));
            throw new RuntimeException("Failed to execute command from ZipEntry: "+zipEntry.getName());
        }
    }



    static void deleteDisplayAnimation(String tag, @Nullable Player deleter){
        if (!DisplayEntityPlugin.isLocalEnabled()) return;
        File saveFile = new File(animSaveFolder, "/"+tag+DisplayAnimation.fileExtension);
        if (saveFile.exists()){
            saveFile.delete();
            if (deleter != null){
                deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.LIGHT_PURPLE+"Successfully deleted from local files!");
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Animation does not exist in local files!");
        }
    }

    static DisplayAnimation retrieveDisplayAnimation(String tag){
        if (!DisplayEntityPlugin.isLocalEnabled()){
            return null;
        }
        File saveFile = new File(animSaveFolder, "/"+tag+DisplayAnimation.fileExtension);
        if (!saveFile.exists()){
            return null;
        }
        return DisplayAnimationManager.getAnimation(saveFile);
    }

    static List<String> getDisplayEntityTags(){
        List<String> tags = new ArrayList<>();
        if (!DisplayEntityPlugin.isLocalEnabled() || groupSaveFolder.listFiles() == null){
            return tags;
        }
        for (File file : groupSaveFolder.listFiles()){
            if (file.getName().contains(DisplayEntityGroup.fileExtension)){
                tags.add(file.getName().replace(DisplayEntityGroup.fileExtension, ""));
            }

        }
        return tags;
    }

    static List<String> getDisplayAnimationTags(){
        List<String> tags = new ArrayList<>();
        File animFolder = new File(animSaveFolder, "/");
        if (!DisplayEntityPlugin.isLocalEnabled() || !animFolder.exists() || animFolder.listFiles() == null){
            return tags;
        }
        for (File file : animFolder.listFiles()){
            if (file.getName().contains(DisplayAnimation.fileExtension)){
                tags.add(file.getName().replace(DisplayAnimation.fileExtension, ""));
            }

        }
        return tags;
    }


    /**
     * Get the save Folder for Locally Saved DisplayEntityGroups
     * @return Save Folder File
     */
    public static File getGroupSaveFolder() {
        return groupSaveFolder;
    }


    /**
     * Get the save Folder for Locally Saved DisplayAnimations
     * @return Save Folder File
     */
    public static File getAnimationSaveFolder() {
        return animSaveFolder;
    }

    public static File getAnimationDatapackFolder(){
        return animDatapackFolder;
    }
}
