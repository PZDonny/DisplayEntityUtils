package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

public final class GroupCMD extends ConsoleUsableSubCommand {

    private static final HashMap<String, DEUSubCommand> subCommands = new HashMap<>();


    public GroupCMD(){
        super(Permission.HELP);
        subCommands.put("help", new GroupHelpCMD());
        subCommands.put("selectnearest", new GroupSelectCMD());
        subCommands.put("deselect", new GroupDeselectCMD());
        subCommands.put("save", new GroupSaveCMD());
        subCommands.put("delete", new GroupDeleteCMD());
        subCommands.put("spawn", new GroupSpawnCMD());
        subCommands.put("despawn", new GroupDespawnCMD());
        subCommands.put("info", new GroupInfoCMD());
        subCommands.put("settag", new GroupSetTagCMD());
        subCommands.put("yaw", new GroupYawCMD());
        subCommands.put("pitch", new GroupPitchCMD());
        subCommands.put("scale", new GroupScaleCMD());
        subCommands.put("brightness", new GroupBrightnessCMD());
        subCommands.put("movehere", new GroupMoveHereCMD());
        subCommands.put("move", new GroupMoveCMD());
        subCommands.put("translate", new GroupTranslateCMD());
        subCommands.put("ungroupinteractions", new GroupUngroupInteractionsCMD());
        subCommands.put("merge", new GroupMergeCMD());
        subCommands.put("addtarget", new GroupAddTargetCMD());
        subCommands.put("clone", new GroupCloneCMD());
        subCommands.put("clonehere", new GroupCloneHereCMD());
        subCommands.put("glow", new GroupGlowCMD());
        subCommands.put("unglow", new GroupUnglowCMD());
        subCommands.put("glowcolor", new GroupGlowColorCMD());
        subCommands.put("copypose", new GroupCopyPoseCMD());
        subCommands.put("setspawnanim", new GroupSetSpawnAnimationCMD());
        subCommands.put("unsetspawnanim", new GroupUnsetSpawnAnimationCMD());
        subCommands.put("togglepersist", new GroupPersistCMD());
        subCommands.put("togglepersistoverride", new GroupPersistenceOverrideCMD());
        subCommands.put("billboard", new GroupBillboardCMD());
        subCommands.put("viewrange", new GroupViewRangeCMD());
        subCommands.put("ride", new GroupRideCMD());
        subCommands.put("dismount", new GroupDismountCMD());
    }

    public static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
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
            CMDUtils.sendCMD(sender, "/mdis group selectnearest <interaction-distance>", "Select the nearest model group and distance to search for interactions");
            CMDUtils.sendCMD(sender, "/mdis group deselect", "Clear your group selection");
            CMDUtils.sendCMD(sender, "/mdis group info", "List information about your selected group");
            CMDUtils.sendCMD(sender, "/mdis group spawn <group-tag> <storage>", "Spawn a saved DisplayEntityGroup from a storage location");
            CMDUtils.sendCMD(sender, "/mdis group despawn", "Despawn your selected group");
            CMDUtils.sendCMD(sender, "/mdis group save <storage-location>", "Save your selected group");
        }
        else if (page == 2){
            CMDUtils.sendCMD(sender, "/mdis group delete <group-tag> <storage-location>", "Delete a saved group from a storage location");
            CMDUtils.sendCMD(sender, "/mdis group addtarget", "Add a targeted interaction entity to your group");
            CMDUtils.sendCMD(sender, "/mdis group ungroupinteractions", "Remove all interactions from your group");
            CMDUtils.sendCMD(sender, "/mdis group settag <group-tag>", "Set this group's tag, or identifier");
            CMDUtils.sendCMD(sender, "/mdis group yaw <yaw> [-pivot]","Set your selected group's yaw, \"-pivot\" pivots interaction entities around the group");
            CMDUtils.sendCMD(sender, "/mdis group pitch <pitch>", "Set your selected group's pitch");
            CMDUtils.sendCMD(sender, "/mdis group scale <scale-multiplier> <tick-duration>", "Scale your selected group");
        }
        else if (page == 3){
            CMDUtils.sendCMD(sender, "/mdis group brightness <block> <sky>", "Set your selected group's brightness. Enter values between 0-15. -1 resets");
            CMDUtils.sendCMD(sender, "/mdis group clone", "Spawn a cloned group at your selected group's location");
            CMDUtils.sendCMD(sender, "/mdis group clonehere", "Spawn a cloned group at your location");
            CMDUtils.sendCMD(sender, "/mdis group move <direction> <distance> <tick-duration>", "Change the actual location of your selected group");
            CMDUtils.sendCMD(sender, "/mdis group translate <direction> <distance> <tick-duration>","Changes your selected group's translation, use \"move\" instead if this group uses animations");
            CMDUtils.sendCMD(sender, "/mdis group movehere", "Change your selected group's actual location to your location");
            CMDUtils.sendCMD(sender, "/mdis group merge <distance>","Merge groups near your selected group");
        }
        else if (page == 4){
            CMDUtils.sendCMD(sender, "/mdis group copypose", "Copies the transformations of the group you're closest to, to your selected group");
            CMDUtils.sendCMD(sender, "/mdis group billboard <fixed | vertical | horizontal | center>", "Set the billboard of all parts in this group");
            CMDUtils.sendCMD(sender, "/mdis group glow", "Make all parts in this group glow");
            CMDUtils.sendCMD(sender, "/mdis group unglow", "Remove the glowing effect from all parts in this group");
            CMDUtils.sendCMD(sender, "/mdis group glowcolor <color | hex-code>", "Set the glow color for all parts in this group");
            CMDUtils.sendCMD(sender, "/mdis group ride <-target | player-name | entity-uuid> [group-tag] [storage] [controller-id]", "Make a group ride an entity. Values in brackets [] are optional");
            CMDUtils.sendCMD(sender, "/mdis group dismount <-target | -selected | player-name | entity-uuid> [-despawn]", "Dismount a group from an entity, with optional despawning");
        }
        else{
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
