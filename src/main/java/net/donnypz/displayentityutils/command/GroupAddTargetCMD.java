package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.command.FramePointDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class GroupAddTargetCMD extends PlayerSubCommand {
    GroupAddTargetCMD() {
        super(Permission.GROUP_ADD_TARGET);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        Entity entity = player.getTargetEntity(5);
        if (!(entity instanceof Interaction interaction)) {
            player.sendMessage(Component.text("Your targeted entity must be an interaction entity within 5 blocks of you", NamedTextColor.RED));
            return;
        }
        /*if (FramePointDisplay.isRelativePointEntity(entity)){
            player.sendMessage(Component.text("Your cannot add the interaction entity from a point preview!", NamedTextColor.RED));
            return;
        }*/
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(interaction);
        if (part != null) {
            if (part.getGroup() == group) {
                player.sendMessage(Component.text("That interaction entity is already apart of your selected group!", NamedTextColor.RED));
            } else {
                part.setGroup(group);
            }
            return;
        }
        else {
            group.addInteractionEntity(interaction);
        }
        player.sendMessage(Component.text("Successfully added interaction entity to your selected group!", NamedTextColor.GREEN));
    }
}
