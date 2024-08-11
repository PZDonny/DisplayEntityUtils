package net.donnypz.displayentityutils.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class InteractionSetWidthCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_DIMENSION)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis interaction setwidth <width>", NamedTextColor.RED));
            return;
        }
        InteractionSetHeightCMD.setInteractionDimensions(player, args, "width");
    }
}
