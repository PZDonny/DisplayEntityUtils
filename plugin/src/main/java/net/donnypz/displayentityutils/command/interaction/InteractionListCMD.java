package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

class InteractionListCMD extends PlayerSubCommand {
    InteractionListCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("listcmd", parentSubCommand, Permission.INTERACTION_LIST_CMD);
    }

    @Override
    public void execute(Player player, String[] args) {

        InteractionCMD.SelectedInteraction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }
        player.sendMessage(DisplayAPI.pluginPrefixLong);
        player.sendMessage(Component.text("Interaction Commands:", NamedTextColor.GRAY));

        List<InteractionCommand> commands = interaction.getInteractionCommandsWithData();
        for (InteractionCommand cmd : commands) {
            String clickType = cmd.isLeftClick() ? "LEFT" : "RIGHT";
            String execType = cmd.isConsoleCommand() ? "CONSOLE" : "PLAYER";
            Component preInfo = Component.text("- [" + clickType + " | " + execType + "] ");
            Component command = Component.text(cmd.getCommand(), NamedTextColor.YELLOW).appendSpace();
            Component remove;
            if (player.hasPermission(Permission.INTERACTION_REMOVE_CMD.getPermission())) {
                remove = Component.text("[REMOVE]", NamedTextColor.RED, TextDecoration.UNDERLINED).clickEvent(ClickEvent.callback(click -> {
                    click.sendMessage(Component.text("Command Removed! ", NamedTextColor.RED).append(Component.text(cmd.getCommand(), NamedTextColor.GRAY)));
                    interaction.removeInteractionCommand(cmd);
                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).build()));
            } else {
                remove = Component.empty();
            }

            player.sendMessage(preInfo.append(command).append(remove));
        }
    }
}
