package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionScaleCMD extends PlayerSubCommand {
    InteractionScaleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("scale", parentSubCommand, Permission.INTERACTION_DIMENSION);
        setTabComplete(2, "<height>");
        setTabComplete(3, "<width>");
        setTabComplete(4, "[tick-duration]");
        setTabComplete(5, "[tick-delay]");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu interaction scale <height> <width> [tick-duration] [tick-delay]", NamedTextColor.RED)));
            return;
        }

        InteractionCMD.SelectedInteraction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }
        float height;
        float width;
        try{
            height = Float.parseFloat(args[2]);
            width = Float.parseFloat(args[3]);
            if (height == 0 || width == 0){
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| The height nor width can be 0", NamedTextColor.GRAY));
            return;
        }

        int duration;
        int delay;
        try{
            duration = args.length >= 5 ? Integer.parseInt(args[4]) : 0;
            delay = args.length >= 6 ? Integer.parseInt(args[5]) : 0;
            if (duration < 0 || delay < 0){
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Duration and delay must be positive whole numbers.", NamedTextColor.GRAY));
            return;
        }

        interaction.scale(height, width, duration, delay);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Scaling Interaction Entity over "+duration+" ticks!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("| Height: "+height, NamedTextColor.GRAY));
        player.sendMessage(Component.text("| Width: "+width, NamedTextColor.GRAY));
        player.sendMessage(Component.text("| Delay: "+delay, NamedTextColor.GRAY));
    }
}