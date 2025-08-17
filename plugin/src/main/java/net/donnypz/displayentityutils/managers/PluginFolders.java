package net.donnypz.displayentityutils.managers;


import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PluginFolders{
    public static final File groupSaveFolder = new File(DisplayAPI.getPlugin().getDataFolder(), "/savedentities/");
    public static final File animSaveFolder = new File(DisplayAPI.getPlugin().getDataFolder(), "/animations/");
    public static final File animDatapackFolder = new File(DisplayAPI.getPlugin().getDataFolder(), "/bdenginedatapacks/");
    public static final File bdeFilesFolder = new File(DisplayAPI.getPlugin().getDataFolder(), "/bdenginefiles/");
    public static final File displayControllerFolder = new File(DisplayAPI.getPlugin().getDataFolder(), "/displaycontrollers/");

    public static void createLocalSaveFolders(JavaPlugin plugin){
        if (!groupSaveFolder.exists()){
            groupSaveFolder.mkdirs();
        }
        if (!animSaveFolder.exists()){
            animSaveFolder.mkdirs();
        }
        if (!animDatapackFolder.exists()){
            animDatapackFolder.mkdirs();
        }
        if (!bdeFilesFolder.exists()){
            bdeFilesFolder.mkdirs();
        }
        if (!displayControllerFolder.exists()){
            displayControllerFolder.mkdirs();
        }

        //Always replace example controller w/ updated version
        String exampleController = "examplecontroller.yml";
        File exampleFile = new File(displayControllerFolder, exampleController);
        InputStream stream = plugin.getResource(exampleController);
        try {
            Files.copy(stream, exampleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            stream.close();
        } catch (IOException e) {}

    }

}
