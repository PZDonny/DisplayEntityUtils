package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsYawCMD extends PlayerSubCommand {

    PartsYawCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("yaw", parentSubCommand, Permission.PARTS_TRANSFORM);
        setTabComplete(2, "<yaw>");
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedMultiSelection(player, selection)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts yaw <yaw>", NamedTextColor.RED));
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        try{
            float yaw = Float.parseFloat(args[2]);
            ActivePart part = selection.getSelectedPart();
            double oldYaw = part.getYaw();
            part.setYaw(yaw, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Yaw set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Yaw: "+oldYaw, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the yaw!", NamedTextColor.RED)));
        }
    }
}
