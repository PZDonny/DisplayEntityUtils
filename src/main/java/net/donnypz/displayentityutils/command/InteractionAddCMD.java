package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class InteractionAddCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_ADD_CMD)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis interaction addcommand <command>", NamedTextColor.RED));
            return;
        }

        Interaction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < args.length; i++){
            builder.append(args[i]);
            if (i+1 != args.length) builder.append(" ");
        }
        String command = builder.toString();
        DisplayUtils.addInteractionCommand(interaction, command);
        int cmdID = DisplayUtils.getInteractionCommands(interaction).size()-1;
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Command Added! "+ChatColor.YELLOW+"(ID: "+cmdID+" | "+command+")");
    }
}
