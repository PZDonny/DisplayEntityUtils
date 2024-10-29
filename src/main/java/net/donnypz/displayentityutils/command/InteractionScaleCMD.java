package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class InteractionScaleCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_DIMENSION)){
            return;
        }

        if (args.length < 6){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction scale <height> <width> <tick-duration> <tick-delay>", NamedTextColor.RED)));
            return;
        }

        Interaction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }
        try{
            float height = Float.parseFloat(args[2]);
            float width = Float.parseFloat(args[3]);
            int duration = Integer.parseInt(args[4]);
            int delay = Integer.parseInt(args[5]);
            if (duration < 0 || delay < 0){
                throw new NumberFormatException();
            }
            DisplayUtils.scaleInteraction(interaction, height, width, duration, delay);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Scaling Interaction Entity over "+duration+" ticks!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Delay: "+delay, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Duration and Delay must be positive whole numbers. Width must be positive", NamedTextColor.GRAY));
        }
    }
}
