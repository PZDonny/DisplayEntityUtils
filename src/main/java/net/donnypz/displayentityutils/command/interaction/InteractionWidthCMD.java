package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class InteractionWidthCMD extends PlayerSubCommand {
    InteractionWidthCMD() {
        super(Permission.INTERACTION_DIMENSION);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis interaction width <width>", NamedTextColor.RED));
            return;
        }
        InteractionHeightCMD.setInteractionDimensions(player, args, "width");
    }
}
