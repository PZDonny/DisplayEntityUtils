package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

class TextSeeThroughCMD extends PlayerSubCommand {
    TextSeeThroughCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("seethrough", parentSubCommand, Permission.TEXT_TOGGLE_SEE_THROUGH);
    }

    @Override
    public void execute(Player player, String[] args) {
        ServerSideSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }
        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
        }

        SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        TextDisplay display = (TextDisplay) selected.getEntity();
        display.setSeeThrough(!display.isSeeThrough());
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully toggled see through of text display!", NamedTextColor.GREEN)));
    }
}
