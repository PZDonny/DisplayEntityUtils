package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.command.gizmo.GizmoCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class GroupCMD extends ParentSubCommand{

    public GroupCMD(){
        super("group");
        this.subCommands.put("list", new ListCMD(
                Component.text("Incorrect Usage! /deu group list <storage> [page-number]", NamedTextColor.RED),
                3,
                true));
        new GroupSelectCMD(this);
        new GroupSelectNearestCMD(this);
        new GroupSelectPlacedCMD(this);
        new GroupDeselectCMD(this);
        new GroupSaveCMD(this);
        new GroupSaveJsonCMD(this);
        new GroupToPacketCMD(this);
        new GroupSelectPacketCMD(this);
        new GroupHidePersistentPacketGroupsCMD(this);
        new GroupShowPersistentPacketGroupsCMD(this);
        new GroupDeleteCMD(this);
        new GroupSpawnCMD(this);
        new GroupSpawnAtCMD(this);
        new GroupSpawnJSONCMD(this);
        new GroupDespawnCMD(this);
        new GroupDespawnAtCMD(this);
        new GroupInfoCMD(this);
        new GroupSetTagCMD(this);
        new GroupYawCMD(this);
        new GroupPitchCMD(this);
        new GroupScaleCMD(this);
        new GroupRotateCMD(this);
        new GroupSetRotationCMD(this);
        new GroupBrightnessCMD(this);
        new GroupMoveHereCMD(this);
        new GroupMoveCMD(this);
        new GroupTranslateCMD(this);
        new GroupUngroupInteractionsCMD(this);
        new GroupMergeCMD(this);
        new GroupAddTargetCMD(this);
        new GroupCloneCMD(this);
        new GroupGlowCMD(this);
        new GroupUnglowCMD(this);
        new GroupGlowColorCMD(this);
        new GroupSetSpawnAnimationCMD(this);
        new GroupUnsetSpawnAnimationCMD(this);
        new GroupPersistCMD(this);
        new GroupPersistenceOverrideCMD(this);
        new GroupBillboardCMD(this);
        new GroupViewRangeCMD(this);
        new GroupRideCMD(this);
        new GroupRideDespawnCMD(this);
        new GroupSafeDismountCMD(this);
        new GroupDismountCMD(this);
        new GroupWorldEditCMD(this);
        new GroupAutoCullCMD(this);
        new GroupRemoveCullCMD(this);
    }

    public static boolean selectGroupSilentSuccess(Player player,
                                                   ActiveGroup<?> group,
                                                   boolean isAutoSelect,
                                                   boolean hidePoints){
        if (!group.isValid()){
            player.sendMessage(Component.text("Group no longer spawned or is invalid.", NamedTextColor.RED));
            return false;
        }

        if (!group.isSelectable()){
            if (isAutoSelect){
                player.sendMessage(DisplayAPI.pluginPrefix
                        .append(Component.text("Failed to automatically select an unselectable group!", NamedTextColor.RED)));
            }
            else{
                player.sendMessage(DisplayAPI.pluginPrefix
                        .append(Component.text("That group is unselectable!", NamedTextColor.RED)));
            }
            return false;
        }

        if (!DisplayGroupManager.setSelectedGroup(player, group)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to select group! Another player already has the group selected!", NamedTextColor.RED)));
            return false;
        }

        if (hidePoints) RelativePointUtils.removeRelativePoints(player);

        glowGroup(player, group);
        GizmoCMD.selectShowGizmo(player, group.getLocation());
        return true;
    }

    public static boolean selectGroup(Player player,
                            ActiveGroup<?> group,
                            boolean isAutoSelect,
                            boolean hidePoints){
        boolean result = selectGroupSilentSuccess(player, group, isAutoSelect, hidePoints);
        if (!result) return false;
        if (isAutoSelect){
            player.sendMessage(Component.text("Your spawned group has been automatically selected", NamedTextColor.GRAY));
        }
        else{
            String groupTag = group.getTag();
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage
                    .miniMessage()
                    .deserialize("<green>Group selected!" + (groupTag == null ? "" : " <white>(Tagged: "+groupTag+")"))));
        }

        return true;
    }

    private static void glowGroup(Player player, ActiveGroup<?> group){
        final int GLOW_DURATION = 30;

        group.glowAndMarkInteractions(player, GLOW_DURATION);
        DisplayAPI.getScheduler().partRunTimer(group.getMasterPart(), new Scheduler.SchedulerRunnable() {
            final int MAX_ITERATIONS = GLOW_DURATION/2;
            int iteration = 0;
            @Override
            public void run() {
                if (iteration == MAX_ITERATIONS || !group.isValid()){
                    cancel();
                    return;
                }
                try{
                    Location groupLoc = group.getLocation();
                    player.spawnParticle(Particle.END_ROD, groupLoc, 1, 0,0,0,0.01);
                    iteration++;
                }
                catch(NullPointerException e){
                    cancel();
                }
            }
        }, 0, 2);
    }


}
