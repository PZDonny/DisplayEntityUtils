package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;

class InteractionListCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_LIST_CMD)){
            return;
        }

        Interaction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        player.sendMessage(Component.text("Interaction Commands:", NamedTextColor.GRAY));

        List<InteractionCommand> commands = DisplayUtils.getInteractionCommandsWithData(interaction);
        for (int i = 0; i < commands.size(); i++){
            InteractionCommand cmd = commands.get(i);
            String clickType = cmd.isLeftClick() ? "LEFT" : "RIGHT";
            String execType = cmd.isConsoleCommand() ? "CONSOLE" : "PLAYER";
            Component preInfo = Component.text("- <"+clickType+" | "+execType+"> ");
            Component command = Component.text(cmd.getCommand()+" ", NamedTextColor.YELLOW);
            Component remove;
            if (player.hasPermission(Permission.INTERACTION_REMOVE_CMD.getPermission())){
                remove = Component.text("Click to REMOVE", NamedTextColor.RED, TextDecoration.UNDERLINED).clickEvent(ClickEvent.callback(click -> {
                    click.sendMessage(Component.text("Command Removed! ", NamedTextColor.RED).append(Component.text(cmd.getCommand(), NamedTextColor.GRAY)));;
                    DisplayUtils.removeInteractionCommand(interaction, cmd.getCommand(), cmd.getKey());
                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).build()));
            }

            else{
                remove = Component.empty();
            }

            player.sendMessage(preInfo.append(command).append(remove));
        }
    }
}
