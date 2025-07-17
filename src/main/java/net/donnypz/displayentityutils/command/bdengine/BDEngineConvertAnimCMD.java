package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class BDEngineConvertAnimCMD extends PlayerSubCommand {
    BDEngineConvertAnimCMD() {
        super(Permission.BDENGINE_CONVERT_ANIM);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 5) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis bdengine convertanim <datapack-name> <group-tag-to-set> <anim-tag-prefix-to-set>", NamedTextColor.RED)));
            player.sendMessage(Component.text("Use \"-\" for the group tag if you do not want to save the group", NamedTextColor.GRAY));
            return;
        }

        String datapackName = args[2];
        String groupTag = args[3];
        String animTag = args[4];
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Attempting to convert animation...", NamedTextColor.AQUA)));
        player.sendMessage(Component.text(" | DO NOT LEAVE THIS AREA UNTIL CONVERSION IS COMPLETED/FAILS", NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" | Conversion time may vary.", NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" | Entities may not be visible while converting", NamedTextColor.YELLOW));
        LocalManager.saveDatapackAnimation(player, datapackName, groupTag, animTag);
    }
}
