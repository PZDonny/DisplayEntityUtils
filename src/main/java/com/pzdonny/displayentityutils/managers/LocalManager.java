package com.pzdonny.displayentityutils.managers;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class LocalManager {
    static File saveFolder = new File(DisplayEntityPlugin.getInstance().getDataFolder(), "/savedentities/");

    private LocalManager(){
    }

    static boolean saveDisplayEntityGroup(DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        if (!DisplayEntityPlugin.isLocalEnabled()) return false;
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(displayEntityGroup);
            byte[] data = byteOut.toByteArray();

            File saveFile = new File(saveFolder, "/"+displayEntityGroup.getTag()+".deg");
            if (saveFile.exists()){
                if (DisplayEntityPlugin.overrideExistingSaves()){
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
        File saveFile = new File(saveFolder, "/"+tag+".deg");
        if (saveFile.exists()){
            saveFile.delete();
            if (deleter != null){
                deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.LIGHT_PURPLE+"Successfully deleted from local files!");
                return;
            }
        }
        if (deleter != null){
            deleter.sendMessage(ChatColor.WHITE+"- "+ChatColor.RED+"Saved Display Entity does not exist in local files!");
        }
    }

    static DisplayEntityGroup retrieveDisplayEntityGroup(String tag){
        if (!DisplayEntityPlugin.isLocalEnabled()) return null;
        try{
            File saveFile = new File(saveFolder, "/"+tag+".deg");
            if (!saveFile.exists()){
                return null;
            }
            FileInputStream fileIn = new FileInputStream(saveFile);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            return (DisplayEntityGroup) objIn.readObject();
        }
        catch(IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            return null;
        }
    }

    static List<String> getDisplayEntityTags(){
        List<String> tags = new ArrayList<>();
        if (!DisplayEntityPlugin.isLocalEnabled() || saveFolder.listFiles() == null) return tags;
        for (File file : saveFolder.listFiles()){
            if (file.getName().contains(".deg")){
                tags.add(file.getName().replace(".deg", ""));
            }

        }
        return tags;
    }


    /**
     * Get the save Folder for Locally Saved DisplayEntityGroups
     * @return Save Folder File
     */
    public static File getSaveFolder() {
        return saveFolder;
    }

}
