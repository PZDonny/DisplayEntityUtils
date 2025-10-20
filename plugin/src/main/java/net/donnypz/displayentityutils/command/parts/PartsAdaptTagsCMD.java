package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
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
        if (DisplayGroupManager.getSelectedGroup(player) instanceof PacketDisplayEntityGroup){
            DisplayEntityPluginCommand.disallowPacketGroup(player);
            return;
        }

        boolean removeFromSB;
        if (args.length < 3){
            removeFromSB = false;
        }
        else{
            removeFromSB = args[2].equalsIgnoreCase("-remove");
        }

        ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null || !selection.isValid()){ //Adapt for all parts
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid part selection! Please try again!", NamedTextColor.RED)));
            return;
        }
        if (PartsCMD.isUnwantedSingleSelection(player, selection)){
            return;
        }

        for (SpawnedDisplayEntityPart part : ((SpawnedPartSelection) selection).getSelectedParts()){
            part.adaptScoreboardTags(removeFromSB);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Adapted all scoreboard tags in your part selection!", NamedTextColor.GREEN)));
    }

}
