package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MannequinBelowNameDistanceCMD extends PartsSubCommand {
    MannequinBelowNameDistanceCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("belownamedistance", parentSubCommand, Permission.MANNEQUIN_NAME);
        setTabComplete(2, "<distance>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        if (!VersionUtils.hasBelowNameDistance()){
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("This attribute does not exist in your current server version!", NamedTextColor.RED)));
        }
        try{
            double distance = Double.parseDouble(args[2]);
            selectedPart.setMannequinBelowNameDistance(distance);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set mannequin below name distance!", NamedTextColor.GREEN)));
        }
        catch(IllegalArgumentException e){
            incorrectUsage(player);
        }
        return true;
    }

    @Override
    protected String getDescription() {
        return "Set the distance that text below your selected mannequin's name can be seen";
    }
}
