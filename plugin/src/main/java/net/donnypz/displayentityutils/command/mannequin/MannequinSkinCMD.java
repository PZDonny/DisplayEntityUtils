package net.donnypz.displayentityutils.command.mannequin;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MannequinSkinCMD extends PartsSubCommand {
    MannequinSkinCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("skin", parentSubCommand, Permission.MANNEQUIN_SKIN, 3, 3);
        setTabComplete(2, "<player-name>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage /deu mannequin skin <name> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String name = args[2];
        ResolvableProfile profile = ResolvableProfile.resolvableProfile(Bukkit.createProfile(name));
        for (ActivePart p : selection.getSelectedParts()){
            p.setMannequinProfile(profile);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set skin of ALL selected mannequins!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        String name = args[2];
        ResolvableProfile profile = ResolvableProfile.resolvableProfile(Bukkit.createProfile(name));
        selectedPart.setMannequinProfile(profile);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set mannequin skin!", NamedTextColor.GREEN)));
        return true;
    }
}
