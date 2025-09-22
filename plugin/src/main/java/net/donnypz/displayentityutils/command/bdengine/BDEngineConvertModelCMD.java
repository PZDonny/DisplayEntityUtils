package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class BDEngineConvertModelCMD extends PlayerSubCommand {

    BDEngineConvertModelCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("convertmodel", parentSubCommand, Permission.BDENGINE_CONVERT_FILE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis bdengine convertmodel <file-name> <group-tag>", NamedTextColor.RED)));
            return;
        }
        String fileName = args[2];
        String groupTag = args[3];
        Location spawnLoc = player.getLocation();
        BDEModel model = BDEngineUtils.readFile(new File(PluginFolders.bdeFilesFolder, BDEngineConvertFileCMD.correctedFileName(fileName)), groupTag, null);
        if (model == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to BDEngine project file!", NamedTextColor.RED)));
            return;
        }

        SpawnedDisplayEntityGroup g = model.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Spawned a BDEngine model from its project file", NamedTextColor.GREEN)));
        if (player.isConnected() && DisplayConfig.autoSelectGroups()){
            g.addPlayerSelection(player);
            player.sendMessage(Component.text("The converted group has been automatically selected", NamedTextColor.GRAY));
        }
    }

}
