package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.SinglePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.relativepoints.DisplayEntitySelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

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
                Entity entity = player.getTargetEntity(10);
                if (!(entity instanceof Interaction)) {
                    player.sendMessage(Component.text("Your targeted entity must be an interaction entity within 10 blocks of you", NamedTextColor.RED));
                }
                else{
                    DisplayEntitySelector.select(player, entity);
                }
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
