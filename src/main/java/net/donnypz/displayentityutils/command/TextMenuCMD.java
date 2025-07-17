package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.VersionUtils;
import net.donnypz.displayentityutils.utils.dialogs.TextDisplayDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class TextMenuCMD extends PlayerSubCommand {
    TextMenuCMD() {
        super(Permission.TEXT_MENU);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (partSelection.getSelectedParts().isEmpty()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        boolean legacy = args.length >= 3 && args[2].equals("-&");
        if (VersionUtils.canViewDialogs(player, true)){
            TextDisplayDialog.sendDialog(player, selected, !legacy);
        }
    }
}
