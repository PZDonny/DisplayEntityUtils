package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public final class LocalManager {
    static final File groupSaveFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/savedentities/");
    static final File animSaveFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/animations/");
    static final File animDatapackFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/bdenginedatapacks/");
    static final File displayControllerFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/displaycontrollers/");
    public static final String datapackConvertDeleteSubParentTag = "deu_delete_sub_parent";
    public static final String datapackUngroupedAddLaterTag = "deu_add_later";
    static final CommandSender silentSender = Bukkit.createCommandSender(feedback -> {});

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
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display entity group locally!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }

            }
            saveFile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            fileOut.write(data);
            fileOut.close();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved display entity group locally!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display entity group locally!"));
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
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted from local files!"));
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved Display Entity Group does not exist in local files!"));
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
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display animation locally!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }

            }
            saveFile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            fileOut.write(data);
            fileOut.close();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved display animation locally!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display animation locally!"));
            }
            return false;
        }
    }

    public static void saveDatapackAnimation(Player player, String datapackName, @NotNull String groupSaveTag, @NotNull String animationSaveTag){
        if (!datapackName.endsWith(".zip")){
            datapackName = datapackName+".zip";
        }
        new DatapackConverter(player, datapackName, groupSaveTag, animationSaveTag);
    }

    public static void saveDatapackLegacyAnimation(Player player, String datapackName, @NotNull String groupSaveTag, @NotNull String animationSaveTag){
        if (!datapackName.endsWith(".zip")){
            datapackName = datapackName+".zip";
        }
        DatapackLegacyConverter.saveDatapackAnimation(player, datapackName, groupSaveTag, animationSaveTag);
    }



    static void deleteDisplayAnimation(String tag, @Nullable Player deleter){
        if (!DisplayEntityPlugin.isLocalEnabled()) return;
        File saveFile = new File(animSaveFolder, "/"+tag+DisplayAnimation.fileExtension);
        if (saveFile.exists()){
            saveFile.delete();
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted from local files!"));
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved Display Animation does not exist in local files!"));
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


    /**
     * Get the folder for placing animation datapacks for conversion, created from BDEngine
     * @return a file
     */
    public static File getAnimationDatapackFolder(){
        return animDatapackFolder;
    }

    /**
     * Get the folder for creating {@link DisplayController}s through a .yml file
     * @return a file
     */
    public static File getDisplayControllerFolder(){
        return displayControllerFolder;
    }
}
