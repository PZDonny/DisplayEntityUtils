package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupYawCMD extends GroupSubCommand {
    GroupYawCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("yaw", parentSubCommand, Permission.GROUP_TRANSFORM, 3, true);
        setTabComplete(2, "<yaw>");
        setTabComplete(3, "-pivot");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu group yaw <yaw> [-pivot]", NamedTextColor.RED));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        try{
            float yaw = Float.parseFloat(args[2]);
            boolean pivot = false;
            if (args.length > 3){
                if (args[3].equalsIgnoreCase("-pivot")){
                    pivot = true;
                }
            }
            double oldYaw = group.getLocation().getYaw();
            group.setYaw(yaw, pivot);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Yaw set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Yaw: "+oldYaw, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Please enter a valid number!", NamedTextColor.RED)));
        }
    }
}
