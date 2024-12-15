package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

        partSelection.glow(80, false);
        boolean toggle = false;
        boolean isAll = false;

        if (args.length >= 3){
            if (args[2].equalsIgnoreCase("-toggle")){
                toggle = true;
            }
            else if (args[2].equalsIgnoreCase("-all")){
                isAll = true;
            }
            if (args.length >= 4){
                if (args[3].equalsIgnoreCase("-toggle")){
                    toggle = true;
                }
                else if (args[3].equalsIgnoreCase("-all")){
                    isAll = true;
                }
            }
        }

        if (toggle){
            if (!group.getMasterPart().getEntity().isGlowing()){
                if (isAll){
                    partSelection.glow(true);
                }
                else{
                    partSelection.getSelectedPart().glow(100);
                }
            }
            else{
                if (isAll){
                    partSelection.unglow();
                }
                else{
                    partSelection.getSelectedPart().unglow();
                }
            }
        }
        else{
            if (isAll){
                partSelection.glow(100, false);
            }
            else{
                partSelection.getSelectedPart().glow(100);
            }
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Part glowing changes applied!", NamedTextColor.GREEN)));
    }
}
