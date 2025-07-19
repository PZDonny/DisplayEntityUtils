package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionResponsiveCMD extends PlayerSubCommand {
    InteractionResponsiveCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("responsive", parentSubCommand, Permission.INTERACTION_RESPONSIVE);
    }

    @Override
    public void execute(Player player, String[] args) {
        Component result;
        Interaction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }
        if (interaction.isResponsive()){
            result = Component.text("DISABLED", NamedTextColor.RED);
        }
        else{
            result = Component.text("ENABLED", NamedTextColor.GREEN);
        }
        interaction.setResponsive(!interaction.isResponsive());
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Toggled Interaction Responsiveness to ")
                .append(result)));
    }
}
