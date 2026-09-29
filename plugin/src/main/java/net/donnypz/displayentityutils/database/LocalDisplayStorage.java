package net.donnypz.displayentityutils.database;

import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.managers.PluginFolders;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public final class LocalDisplayStorage implements DisplayStorage {

    @Override
    public boolean isEnabled() {
        return LoadMethod.LOCAL.isEnabled();
    }

    @Override
    public boolean saveDisplayEntityGroup(@NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(displayEntityGroup);

            gzipOut.close();
            objOut.close();

            byte[] data = byteOut.toByteArray();

            File saveFile = new File(PluginFolders.groupSaveFolder, "/"+displayEntityGroup.getTag()+DisplayEntityGroup.FILE_EXTENSION);
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

    @Override
    public void deleteDisplayEntityGroup(@NotNull String tag, @Nullable Player deleter){
        File saveFile = new File(PluginFolders.groupSaveFolder, "/"+tag+DisplayEntityGroup.FILE_EXTENSION);
        if (saveFile.exists()){
            saveFile.delete();
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted group from local files!"));
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved display entity group does not exist in local files!"));
        }
    }

    @Override
    public @Nullable DisplayEntityGroup getDisplayEntityGroup(@NotNull String tag){
        File saveFile = new File(PluginFolders.groupSaveFolder, "/"+tag+DisplayEntityGroup.FILE_EXTENSION);
        if (!saveFile.exists()){
            return null;
        }
        return DisplayGroupManager.getGroup(saveFile);
    }

    @Override
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

            File saveFile = new File(PluginFolders.animSaveFolder, "/"+displayAnimation.getAnimationTag()+DisplayAnimation.FILE_EXTENSION);
            if (saveFile.exists()){
                if (DisplayConfig.overwritexistingSaves()){
                    saveFile.delete();
                }
                else{
                    if (saver != null){
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save animation locally!"));
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
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved animation locally!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save animation locally!"));
            }
            return false;
        }
    }




    @Override
    public void deleteDisplayAnimation(@NotNull String tag, @Nullable Player deleter){
        File saveFile = new File(PluginFolders.animSaveFolder, "/"+tag+DisplayAnimation.FILE_EXTENSION);
        if (saveFile.exists()){
            saveFile.delete();
            if (deleter != null){
                deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <light_purple>Successfully deleted animation from local files!"));
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Saved animation does not exist in local files!"));
        }
    }

    @Override
    public @Nullable DisplayAnimation getDisplayAnimation(@NotNull String tag){
        File saveFile = new File(PluginFolders.animSaveFolder, "/"+tag+DisplayAnimation.FILE_EXTENSION);
        if (!saveFile.exists()){
            return null;
        }
        return DisplayAnimationManager.getAnimation(saveFile);
    }

    @Override
    public @NotNull List<String> getGroupTags(){
        return getTags(PluginFolders.groupSaveFolder, DisplayEntityGroup.FILE_EXTENSION);
    }

    @Override
    public @NotNull List<String> getGroupTags(int page, int size) {
        return getTags(PluginFolders.groupSaveFolder, DisplayEntityGroup.FILE_EXTENSION, page, size);
    }

    @Override
    public @NotNull List<String> getAnimationTags(){
        return getTags(PluginFolders.animSaveFolder, DisplayAnimation.FILE_EXTENSION);
    }

    @Override
    public @NotNull List<String> getAnimationTags(int page, int size) {
        return getTags(PluginFolders.animSaveFolder, DisplayAnimation.FILE_EXTENSION, page, size);
    }

    private List<String> getTags(File saveFolder, String fileExtension){
        List<String> tags = new ArrayList<>();
        File animFolder = new File(saveFolder, "/");
        if (!animFolder.exists()) return tags;

        File[] files = animFolder.listFiles();
        if (files == null) return tags;

        for (File file : files){
            String fileName = file.getName();
            if (fileName.endsWith(fileExtension)){
                tags.add(fileName.replace(fileExtension, ""));
            }
        }
        return tags;
    }

    private List<String> getTags(File saveFolder, String fileExtension, int page, int size){
        List<String> tags = new ArrayList<>();

        int offset = CommonDisplayStorageUtils.getPageOffset(page, size);
        int skipped = 0;
        int count = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(saveFolder.toPath())) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path)) continue;

                String fileName = path.getFileName().toString();
                if (!fileName.endsWith(fileExtension)) continue;

                if (skipped++ < offset) continue;

                String tag = fileName.substring(0, fileName.length() - fileExtension.length());
                tags.add(tag);

                if (++count >= size) {
                    break;
                }
            }
        } catch (IOException e) {
            return tags;
        }
            return tags;
    }
}
