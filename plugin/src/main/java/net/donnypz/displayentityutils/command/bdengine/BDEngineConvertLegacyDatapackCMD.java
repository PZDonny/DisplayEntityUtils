package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.bdengine.convert.datapack.BDEngineLegacyDPConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class BDEngineConvertLegacyDatapackCMD extends PlayerSubCommand {
    BDEngineConvertLegacyDatapackCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("convertdpleg", parentSubCommand, Permission.BDENGINE_CONVERT_DATAPACK);
        setTabComplete(2, "<datapack-name>");
        setTabComplete(3, "<group-tag-to-set>");
        setTabComplete(4, "<anim-tag-prefix-to-set>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 5) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis bdengine convertdpleg <datapack-name> <group-tag-to-set> <anim-tag-to-set>", NamedTextColor.RED)));
            player.sendMessage(Component.text("Use \"-\" for the group tag if you do not want to save the group", NamedTextColor.GRAY));
            return;
        }

        String datapackName = args[2];
        String groupTag = args[3];
        String animPrefix = args[4];
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Attempting to convert datapack...", NamedTextColor.AQUA)));
        player.sendMessage(Component.text(" | DO NOT LEAVE THIS AREA UNTIL CONVERSION IS COMPLETED/FAILS", NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" | Conversion time may vary.", NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" | Entities may not be visible while converting", NamedTextColor.YELLOW));
        BDEngineLegacyDPConverter.saveDatapackAnimation(player, datapackName, groupTag, animPrefix);
    }
}
