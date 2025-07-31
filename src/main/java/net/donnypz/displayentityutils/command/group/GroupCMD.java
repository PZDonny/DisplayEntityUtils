package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class GroupCMD extends ConsoleUsableSubCommand {

    public GroupCMD(){
        super(Permission.HELP, new GroupHelpCMD());
        new GroupSelectNearestCMD(this);
        new GroupDeselectCMD(this);
        new GroupSaveCMD(this);
        new GroupToPacketCMD(this);
        new GroupMarkPacketGroupsCMD(this);
        new GroupDeleteCMD(this);
        new GroupSpawnCMD(this);
        new GroupDespawnCMD(this);
        new GroupInfoCMD(this);
        new GroupSetTagCMD(this);
        new GroupYawCMD(this);
        new GroupPitchCMD(this);
        new GroupScaleCMD(this);
        new GroupBrightnessCMD(this);
        new GroupMoveHereCMD(this);
        new GroupMoveCMD(this);
        new GroupTranslateCMD(this);
        new GroupUngroupInteractionsCMD(this);
        new GroupMergeCMD(this);
        new GroupAddTargetCMD(this);
        new GroupCloneCMD(this);
        new GroupCloneHereCMD(this);
        new GroupGlowCMD(this);
        new GroupUnglowCMD(this);
        new GroupGlowColorCMD(this);
        new GroupCopyPoseCMD(this);
        new GroupSetSpawnAnimationCMD(this);
        new GroupUnsetSpawnAnimationCMD(this);
        new GroupPersistCMD(this);
        new GroupPersistenceOverrideCMD(this);
        new GroupBillboardCMD(this);
        new GroupViewRangeCMD(this);
        new GroupRideCMD(this);
        new GroupDismountCMD(this);
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            groupHelp(sender, 1);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            groupHelp(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }


    static void groupHelp(CommandSender sender, int page){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        if (page <= 1){
            CMDUtils.sendCMD(sender, "/mdis group help <page-number>", "Get help for groups");
            CMDUtils.sendCMD(sender, "/mdis group selectnearest <distance>", "Select the nearest group within the given distance");
            CMDUtils.sendCMD(sender, "/mdis group deselect", "Clear your group selection");
            CMDUtils.sendCMD(sender, "/mdis group info", "List information about your selected group");
            CMDUtils.sendCMD(sender, "/mdis group spawn <group-tag> <storage>", "Spawn a saved DisplayEntityGroup from a storage location");
            CMDUtils.sendCMD(sender, "/mdis group despawn", "Despawn your selected group");
            CMDUtils.sendCMD(sender, "/mdis group save <storage-location>", "Save your selected group");
        }
        else if (page == 2){
            CMDUtils.sendCMD(sender, "/mdis group delete <group-tag> <storage-location>", "Delete a saved group from a storage location");
            CMDUtils.sendCMD(sender, "/mdis group topacket [-confirm] [-keep]", "Make your selected group packet-based, making it unselectable. \"-confirm\" confirms the action."+
                    " \"-keep\" keeps the non-packet based version of your group spawned.");
            CMDUtils.sendCMD(sender, "/mdis group markpacketgroups", "Create markers for all packet groups stored in your current chunk");
            CMDUtils.sendCMD(sender, "/mdis group addtarget", "Add a targeted interaction entity to your group");
            CMDUtils.sendCMD(sender, "/mdis group ungroupinteractions", "Remove all interactions from your group");
            CMDUtils.sendCMD(sender, "/mdis group settag <group-tag>", "Set this group's tag, or identifier");
        }
        else if (page == 3){
            CMDUtils.sendCMD(sender, "/mdis group yaw <yaw> [-pivot]","Set your selected group's yaw, \"-pivot\" pivots interaction entities around the group");
            CMDUtils.sendCMD(sender, "/mdis group pitch <pitch>", "Set your selected group's pitch");
            CMDUtils.sendCMD(sender, "/mdis group scale <scale-multiplier> <tick-duration>", "Scale your selected group");
            CMDUtils.sendCMD(sender, "/mdis group brightness <block> <sky>", "Set your selected group's brightness. Enter values between 0-15. -1 resets");
            CMDUtils.sendCMD(sender, "/mdis group clone", "Spawn a cloned group at your selected group's location");
            CMDUtils.sendCMD(sender, "/mdis group clonehere", "Spawn a cloned group at your location");
            CMDUtils.sendCMD(sender, "/mdis group move <direction> <distance> <tick-duration>", "Change the actual location of your selected group");

        }
        else if (page == 4){
            CMDUtils.sendCMD(sender, "/mdis group translate <direction> <distance> <tick-duration>","Changes your selected group's translation, use \"move\" instead if this group uses animations");
            CMDUtils.sendCMD(sender, "/mdis group movehere", "Change your selected group's actual location to your location");
            CMDUtils.sendCMD(sender, "/mdis group merge <distance>","Merge groups near your selected group");
            CMDUtils.sendCMD(sender, "/mdis group copypose", "Copies the transformations of the group you're closest to, to your selected group");
            CMDUtils.sendCMD(sender, "/mdis group billboard <fixed | vertical | horizontal | center>", "Set the billboard of all parts in this group");
            CMDUtils.sendCMD(sender, "/mdis group glow", "Make all parts in this group glow");
            CMDUtils.sendCMD(sender, "/mdis group unglow", "Remove the glowing effect from all parts in this group");
        }
        else{
            CMDUtils.sendCMD(sender, "/mdis group glowcolor <color | hex-code>", "Set the glow color for all parts in this group");
            CMDUtils.sendCMD(sender, "/mdis group ride <-target | player-name | entity-uuid> [group-tag] [storage] [controller-id]", "Make a group ride an entity. Values in brackets [] are optional");
            CMDUtils.sendCMD(sender, "/mdis group dismount <-target | -selected | player-name | entity-uuid> [-despawn]", "Dismount a group from an entity, with optional despawning");
            CMDUtils.sendCMD(sender, "/mdis group viewrange <view-range-multiplier>", "Set the view range multiplier for your selected group");
            CMDUtils.sendCMD(sender, "/mdis group setspawnanim <anim-tag> <storage> <linear | loop>", "Set an animation to play when this group is spawned/loaded");
            CMDUtils.sendCMD(sender, "/mdis group unsetspawnanim", "Remove the spawn animation that's set on your selected group");
            CMDUtils.sendCMD(sender, "/mdis group togglepersist", "Toggle if your group should persist after a server shutdown");
            CMDUtils.sendCMD(sender, "/mdis group togglepersistoverride", "Toggle if your group's persistence can be overriden when loaded by a chunk, " +
                    "only if \"persistenceOverride\" is enabled in the config");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }
    
}
