package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupCloneCMD extends GroupSubCommand {
    GroupCloneCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("clone", parentSubCommand, Permission.GROUP_CLONE, false);
        addFlag("-here");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        Location cloneLoc = getOptionalArguments(player, args).hasFlag("-here")
                ? player.getLocation()
                : group.getLocation();
        clone(player, group.clone(cloneLoc));
    }

    private void clone(Player p, ActiveGroup<?> clonedGroup){
        if (clonedGroup == null){
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to clone your selected group!", NamedTextColor.RED)));
        }
        else{
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Cloned your selected group", NamedTextColor.GREEN)));
            p.sendMessage(Component.text("- Your group selection has been changed to the newly created group", NamedTextColor.GRAY, TextDecoration.ITALIC));
            DisplayGroupManager.setSelectedGroup(p, clonedGroup);
            clonedGroup.glowAndMarkInteractions(p, 80);
        }
    }
}
