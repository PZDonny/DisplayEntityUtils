package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class InteractionInfoCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_INFO)){
            return;
        }

        Interaction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);

        String groupTag = DisplayUtils.getGroupTag(interaction);
        if (groupTag == null){
            groupTag = "<gray>NOT GROUPED";
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("Height: <yellow>"+interaction.getInteractionHeight()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Width: <yellow>"+interaction.getInteractionWidth()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: <yellow>"+groupTag));

    }
}
