package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class PartsUnglowCMD extends PlayerSubCommand {
    PartsUnglowCMD() {
        super(Permission.PARTS_GLOW);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        boolean isAll;

        if (args.length >= 3){
            isAll = args[2].equalsIgnoreCase("-all");
        }
        else{
            isAll = false;
        }

        if (isAll){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed the glow from your selection!", NamedTextColor.YELLOW)));
            partSelection.unglow();
        }
        else{
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed the glow from your selected part!", NamedTextColor.YELLOW)));
            partSelection.getSelectedPart().unglow();
        }

    }
}
