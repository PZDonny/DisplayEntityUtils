package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

class TextShadowCMD extends PlayerSubCommand {
    TextShadowCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("shadow", parentSubCommand, Permission.TEXT_TOGGLE_SHADOW);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
        }

        ActivePart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        selected.setTextDisplayShadowed(!selected.isTextDisplayShadowed());
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully toggled shadow on text display!", NamedTextColor.GREEN)));
    }
}
