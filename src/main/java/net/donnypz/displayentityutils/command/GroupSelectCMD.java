package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class GroupSelectCMD extends PlayerSubCommand {
    GroupSelectCMD() {
        super(Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number for the distance to select interaction entities", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group selectnearest <interaction-distance>", NamedTextColor.GRAY));
            return;
        }

        try {
            double interactionDistance = Double.parseDouble(args[2]);
            GroupResult result = DisplayGroupManager.getSpawnedGroupNearLocation(player.getLocation(), 2.5f, player);
            if (result == null || result.group() == null){
                return;
            }
            SpawnedDisplayEntityGroup group = result.group();

            boolean selectResult = DisplayGroupManager.setSelectedSpawnedGroup(player, group);
            if (selectResult){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully selected group!", NamedTextColor.GREEN)));
            }
            else{
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to select group! Another player already has that group selected!", NamedTextColor.RED)));
                return;
            }

            group.getUnaddedInteractionEntitiesInRange(interactionDistance, true);
            group.glowAndOutline(player, 50);

            if (DEUCommandUtils.removeRelativePoints(player)){
                player.sendMessage(Component.text("Your previewed points have been despawned since you have changed your selected group", NamedTextColor.GRAY, TextDecoration.ITALIC));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number for the distance to select interaction entities", NamedTextColor.RED)));
        }
    }
}
