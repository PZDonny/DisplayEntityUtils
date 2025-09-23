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

public class BDEngineConvertAnimCMD extends PlayerSubCommand {

    BDEngineConvertAnimCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("convertanim", parentSubCommand, Permission.BDENGINE_CONVERT_FILE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis bdengine convertanim <file-name> <anim-tag-prefix>", NamedTextColor.RED)));
            return;
        }
        String fileName = args[2];
        String animPrefix = args[3];
        BDEModel model = BDEngineUtils.readFile(new File(PluginFolders.bdeFilesFolder, BDEngineConvertFileCMD.correctedFileName(fileName)), "", animPrefix);
        if (model == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to BDEngine project file!", NamedTextColor.RED)));
            return;
        }

        List<SpawnedDisplayAnimation> animations = model.getAnimations();
        if (animations.isEmpty()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to find animations with frames in the project file!", NamedTextColor.RED)));
            return;
        }

        for (SpawnedDisplayAnimation anim : animations){
            DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim.toDisplayAnimation(), player);
        }
    }

}
