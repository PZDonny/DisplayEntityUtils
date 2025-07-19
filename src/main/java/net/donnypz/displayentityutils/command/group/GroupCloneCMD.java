package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupCloneCMD extends PlayerSubCommand {
    GroupCloneCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("clone", parentSubCommand, Permission.GROUP_CLONE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        clone(player, group.clone(group.getMasterPart().getEntity().getLocation()));
    }

    static void clone(Player p, SpawnedDisplayEntityGroup clonedGroup){
        if (clonedGroup == null){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to clone spawned display entity group!", NamedTextColor.RED)));
        }
        else{
            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully cloned your selected group", NamedTextColor.GREEN)));
            p.sendMessage(Component.text("- Your group selection has been changed to the newly created group", NamedTextColor.GRAY, TextDecoration.ITALIC));
            DisplayGroupManager.setSelectedSpawnedGroup(p, clonedGroup);
            clonedGroup.glowAndOutline(p, 80);
        }
    }
}
