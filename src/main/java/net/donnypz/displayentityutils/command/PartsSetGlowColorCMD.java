package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

class PartsSetGlowColorCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_GLOW_SET_COLOR)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + "Enter a valid color!");
            player.sendMessage(Component.text("/mdis parts setglowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }

        Color c = GroupSetGlowColorCMD.getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+"Enter a valid color!");
            player.sendMessage(Component.text("/mdis parts setglowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }
        for (SpawnedDisplayEntityPart part : selection.getSelectedParts()){
            part.setGlowColor(c);
        }
        selection.glow(60, true);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Glow color successfully set for selected display entity part(s)!");
    }

}
