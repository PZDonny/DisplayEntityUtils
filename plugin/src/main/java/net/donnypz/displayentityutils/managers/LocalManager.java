package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public final class LocalManager implements DisplayStorage{
    public static final String datapackConvertDeleteSubParentTag = "deu_delete_sub_parent";
    public static final String datapackUngroupedAddLaterTag = "deu_add_later";

    public boolean saveDisplayEntityGroup(@NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(displayEntityGroup);

            gzipOut.close();
            objOut.close();

            byte[] data = byteOut.toByteArray();

            File saveFile = new File(PluginFolders.groupSaveFolder, "/"+displayEntityGroup.getTag()+DisplayEntityGroup.fileExtension);
            if (saveFile.exists()){
                if (!DisplayConfig.overwritexistingSaves()){
                    if (saver != null){
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display entity group locally!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }
                saveFile.delete();
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

    public void deleteDisplayEntityGroup(@NotNull String tag, @Nullable Player deleter){
        File saveFile = new File(PluginFolders.groupSaveFolder, "/"+tag+DisplayEntityGroup.fileExtension);
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

    public @Nullable DisplayEntityGroup getDisplayEntityGroup(@NotNull String tag){
        File saveFile = new File(PluginFolders.groupSaveFolder, "/"+tag+DisplayEntityGroup.fileExtension);
        if (!saveFile.exists()){
            return null;
        }
        return DisplayGroupManager.getGroup(saveFile);
    }

    public boolean saveDisplayAnimation(@NotNull DisplayAnimation displayAnimation, @Nullable Player saver){
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(displayAnimation);
            gzipOut.close();
            objOut.close();

            byte[] data = byteOut.toByteArray();
            byteOut.close();

            File saveFile = new File(PluginFolders.animSaveFolder, "/"+displayAnimation.getAnimationTag()+DisplayAnimation.fileExtension);
            if (saveFile.exists()){
                if (DisplayConfig.overwritexistingSaves()){
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




    public void deleteDisplayAnimation(@NotNull String tag, @Nullable Player deleter){
        File saveFile = new File(PluginFolders.animSaveFolder, "/"+tag+DisplayAnimation.fileExtension);
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

    public @Nullable DisplayAnimation getDisplayAnimation(@NotNull String tag){
        File saveFile = new File(PluginFolders.animSaveFolder, "/"+tag+DisplayAnimation.fileExtension);
        if (!saveFile.exists()){
            return null;
        }
        return DisplayAnimationManager.getAnimation(saveFile);
    }

    public @NotNull List<String> getGroupTags(){
        List<String> tags = new ArrayList<>();
        File groupFolder = new File(PluginFolders.groupSaveFolder, "/");
        if (!groupFolder.exists() || groupFolder.listFiles() == null){
            return tags;
        }
        for (File file : groupFolder.listFiles()){
            if (file.getName().contains(DisplayEntityGroup.fileExtension)){
                tags.add(file.getName().replace(DisplayEntityGroup.fileExtension, ""));
            }

        }
        return tags;
    }

    public @NotNull List<String> getAnimationTags(){
        List<String> tags = new ArrayList<>();
        File animFolder = new File(PluginFolders.animSaveFolder, "/");
        if (!animFolder.exists() || animFolder.listFiles() == null){
            return tags;
        }
        for (File file : animFolder.listFiles()){
            if (file.getName().contains(DisplayAnimation.fileExtension)){
                tags.add(file.getName().replace(DisplayAnimation.fileExtension, ""));
            }

        }
        return tags;
    }
}
