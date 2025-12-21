package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsMoveHereCMD extends PartsSubCommand {

    public PartsMoveHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("movehere", parentSubCommand, Permission.PARTS_TRANSFORM, 2, 2);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }
        super.execute(player, args);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu parts movehere [-all]", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        for (ActivePart part : selection.getSelectedParts()){
            if (part.isDisplay()) continue;
            Location loc = player.getLocation();
            loc.setYaw(part.getYaw());
            loc.setPitch(part.getPitch());
            part.teleport(loc);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved ALL selected non-display parts to your location!", NamedTextColor.GREEN)));
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selectedPart.isDisplay() && group != null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this for grouped display entities!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Use \"/deu parts translate\" instead", NamedTextColor.GRAY));
            return false;
        }

        Location loc = player.getLocation();
        loc.setYaw(selectedPart.getYaw());
        loc.setPitch(selectedPart.getPitch());
        selectedPart.teleport(loc);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved your selected part to your location!", NamedTextColor.GREEN)));
        return true;
    }
}
