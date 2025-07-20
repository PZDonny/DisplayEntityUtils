package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionWidthCMD extends PlayerSubCommand {
    InteractionWidthCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("width", parentSubCommand, Permission.INTERACTION_DIMENSION);
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
