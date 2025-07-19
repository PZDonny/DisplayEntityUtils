package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsAdaptTagsCMD extends PlayerSubCommand {
    PartsAdaptTagsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("adapttags", parentSubCommand, Permission.PARTS_TAG);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        boolean removeFromSB;
        if (args.length < 2){
            removeFromSB = false;
        }
        else{
            removeFromSB = args[1].equalsIgnoreCase("-remove");
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null || partSelection.isValid()){ //Adapt for all parts
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid part selection! Please try again!", NamedTextColor.RED)));
            return;
        }

        for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
            part.adaptScoreboardTags(removeFromSB);
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Adapted all scoreboard tags in your part selection!", NamedTextColor.GREEN)));
    }

}
