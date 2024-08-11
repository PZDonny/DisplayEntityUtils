package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.ArrayList;

class InteractionRemoveCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_REMOVE_CMD)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis interaction removecommand <id>", NamedTextColor.RED));
            return;
        }
        try{
            int id = Integer.parseInt(args[2]);
            Interaction interaction = InteractionCMD.getInteraction(player, true);
            if (interaction == null){
                return;
            }
            ArrayList<String> commands = DisplayUtils.getInteractionCommands(interaction);
            if (commands.size() <= id){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"That command ID does not exist on the selected interaction entity");
                return;
            }
            DisplayUtils.removeInteractionCommand(interaction, commands.get(id));
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Removed command with ID "+id+" from the selected interaction entity");
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number for the command ID you wish to remove");
        }
    }
}
