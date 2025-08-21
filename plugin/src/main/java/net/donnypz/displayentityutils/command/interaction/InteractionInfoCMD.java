package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionInfoCMD extends PlayerSubCommand {
    InteractionInfoCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("info", parentSubCommand, Permission.INTERACTION_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {
        Interaction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefixLong);

        String groupTag = DisplayUtils.getGroupTag(interaction);
        if (groupTag == null){
            groupTag = "<gray>NOT GROUPED";
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("Height: <yellow>"+interaction.getInteractionHeight()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Width: <yellow>"+interaction.getInteractionWidth()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Responsive: "+(interaction.isResponsive() ? "<green>ENABLED" : "<red>DISABLED")));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: <yellow>"+groupTag));
    }
}
