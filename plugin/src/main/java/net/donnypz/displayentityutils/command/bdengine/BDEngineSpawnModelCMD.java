package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BDEngineSpawnModelCMD extends PlayerSubCommand {

    BDEngineSpawnModelCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawnmodel", parentSubCommand, Permission.BDENGINE_SPAWN_MODEL);
        setTabComplete(2, "<file-name>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis bdengine spawnmodel <file-name>", NamedTextColor.RED)));
            return;
        }
        String fileName = args[2];
        Location spawnLoc = player.getLocation();
        BDEModel model = BDEngineUtils.readFile(new File(PluginFolders.bdeFilesFolder, "/"+fileExtension(fileName)));
        if (model == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to read model from the given project file!", NamedTextColor.RED)));
            return;
        }
        model.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Spawned a BDEngine model from its project file", NamedTextColor.GREEN)));
    }

    public static String fileExtension(String fileName){
        return fileName.endsWith(".bdengine") ? fileName : fileName+".bdengine";
    }
}
