package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.relativepoints.DisplayEntitySelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PartsSelectCMD extends PlayerSubCommand {

    PartsSelectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("select", parentSubCommand, Permission.PARTS_SELECT);
        setTabComplete(2, List.of("<distance>", "-target"));
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu parts select <distance | -target>", NamedTextColor.RED)));
            return;
        }
        try{
            String arg = args[2];
            if (arg.equalsIgnoreCase("-target")){
                Entity entity = DisplayEntityPluginCommand.getTargetEntity(player);
                if (entity == null) return;

                DisplayEntitySelector.select(player, entity);
                return;
            }

            double distance = Double.parseDouble(arg);
            if (distance <= 0){
                throw new IllegalArgumentException();
            }
            player.sendMessage(Component.empty());
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Finding display entities within "+distance+" blocks...", NamedTextColor.YELLOW)));
            RelativePointUtils.spawnDisplayEntityPoints(distance, player);
        }
        catch(IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid input! Enter a positive number for the distance, or -target to select a targeted Interaction entity", NamedTextColor.RED));
        }
    }
}
