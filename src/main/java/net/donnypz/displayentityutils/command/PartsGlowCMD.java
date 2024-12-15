package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class PartsGlowCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_GLOW)){
            return;
        }

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
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glowing applied to your selection!", NamedTextColor.GREEN)));
            partSelection.glow(false, true);
        }
        else{
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glowing applied to your selected part!", NamedTextColor.GREEN)));
            partSelection.getSelectedPart().glow(true);
        }

    }
}
