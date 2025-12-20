package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupAddTargetCMD extends PlayerSubCommand {
    GroupAddTargetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addtarget", parentSubCommand, Permission.GROUP_ADD_TARGET);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> g = DisplayGroupManager.getSelectedGroup(player);
        if (g == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        if (!(g instanceof SpawnedDisplayEntityGroup group)){
            DisplayEntityPluginCommand.disallowPacketGroup(player);
            return;
        }


        Entity entity = player.getTargetEntity(10);
        if (!(entity instanceof Interaction || entity instanceof Mannequin)) {
            player.sendMessage(Component.text("Your targeted entity must be an interaction or mannequin entity within 10 blocks of you", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(entity);
        if (part != null) {
            if (part.getGroup() == group) {
                player.sendMessage(Component.text("That entity is already apart of your selected group!", NamedTextColor.RED));
            }
            else {
                part.setGroup(group);
            }
            return;
        }
        else {
            group.addEntity(entity);
            SpawnedPartSelection sel = (SpawnedPartSelection) DisplayGroupManager.getPartSelection(player);
            sel.refresh();
        }
        player.sendMessage(Component.text("Successfully added entity to your selected group!", NamedTextColor.GREEN));
    }
}
