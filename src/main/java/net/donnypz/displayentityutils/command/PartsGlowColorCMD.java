package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.deu.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

class PartsGlowColorCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_GLOW_COLOR)){
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
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis parts glowcolor <color | hex-code> [-all]", NamedTextColor.GRAY));
            return;
        }

        Color c = DEUCommandUtils.getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis parts glowcolor <color | hex-code> [-all]", NamedTextColor.GRAY));
            return;
        }

        if (args.length >= 4 && args[3].equalsIgnoreCase("-all")) {
            selection.setGlowColor(c);
            selection.glow(60, true);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glow color successfully set for selected display entity part(s) in your selection!", NamedTextColor.GREEN)));
        }
        else{
            SpawnedDisplayEntityPart selectedPart = selection.getSelectedPart();
            if (selectedPart.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Interaction entities cannot have a glow color applied!", NamedTextColor.RED)));
            }
            else{
                selectedPart.setGlowColor(c);
                selectedPart.glow(60);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glow color successfully set for your selected part!", NamedTextColor.GREEN)));
            }
        }
    }
}
