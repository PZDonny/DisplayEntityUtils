package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class GroupCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    GroupCMD(){
        subCommands.put("selectnearest", new GroupSelectCMD());
        subCommands.put("deselect", new GroupDeselectCMD());
        subCommands.put("save", new GroupSaveCMD());
        subCommands.put("delete", new GroupDeleteCMD());
        subCommands.put("spawn", new GroupSpawnCMD());
        subCommands.put("despawn", new GroupDespawnCMD());
        subCommands.put("info", new GroupInfoCMD());
        subCommands.put("settag", new GroupSetTagCMD());
        subCommands.put("setyaw", new GroupSetYawCMD());
        subCommands.put("setpitch", new GroupSetPitchCMD());
        subCommands.put("setscale", new GroupSetScaleCMD());
        subCommands.put("movehere", new GroupMoveHereCMD());
        subCommands.put("move", new GroupMoveCMD());
        subCommands.put("translate", new GroupTranslateCMD());
        subCommands.put("ungroupinteractions", new GroupUngroupInteractionsCMD());
        subCommands.put("merge", new GroupMergeCMD());
        subCommands.put("addtarget", new GroupAddTargetCMD());
        subCommands.put("clone", new GroupCloneCMD());
        subCommands.put("clonehere", new GroupCloneHereCMD());
        subCommands.put("glow", new GroupGlowCMD());
        subCommands.put("setglowcolor", new GroupSetGlowColorCMD());
        subCommands.put("copypose", new GroupCopyPoseCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }


    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2){
            groupHelp(player);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
                return;
            }
            groupHelp(player);
        }
        else{
            subCommand.execute(player, args);
        }
    }


    static void groupHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        CMDUtils.sendCMD(sender, "/mdis group selectnearest <interaction-distance>", " (Select the nearest model group and distance to search for interactions)");
        CMDUtils.sendCMD(sender, "/mdis group deselect", " (Clear your group selection)");
        CMDUtils.sendCMD(sender, "/mdis group info", " (List information about your selected group)");
        CMDUtils.sendCMD(sender, "/mdis group spawn <group-tag> <storage>", " (Spawn a saved DisplayEntityGroup from a storage location)");
        CMDUtils.sendCMD(sender, "/mdis group despawn", " (Despawn your selected group)");
        CMDUtils.sendCMD(sender, "/mdis group save <storage-location>", " (Save your selected group)");
        CMDUtils.sendCMD(sender, "/mdis group delete <group-tag> <storage-location>", " (Delete a saved group from a storage location)");
        CMDUtils.sendCMD(sender, "/mdis group addtarget", " (Add a targeted interaction entity to your group)");
        CMDUtils.sendCMD(sender, "/mdis group ungroupinteractions", " (Remove all interactions from your group)");
        CMDUtils.sendCMD(sender, "/mdis group settag <group-tag>", " (Set this group's tag, or identifier)");
        CMDUtils.sendCMD(sender, "/mdis group setyaw <yaw> [-pivot]"," (Set your selected group's yaw, \"-pivot\" pivots interaction entities around the group)");
        CMDUtils.sendCMD(sender, "/mdis group setpitch <pitch>", " (Set your selected group's pitch)");
        CMDUtils.sendCMD(sender, "/mdis group setscale <scale-multiplier> <tick-duration>", "(Scale all parts of your selected group)");
        CMDUtils.sendCMD(sender, "/mdis group clone", " (Spawn a cloned group at your selected group's location)");
        CMDUtils.sendCMD(sender, "/mdis group clonehere", " (Spawn a cloned group at your location)");
        CMDUtils.sendCMD(sender, "/mdis group move <direction> <distance> <tick-duration>", " (Change the actual location of your selected group)");
        CMDUtils.sendCMD(sender, "/mdis group translate <direction> <distance> <tick-duration>"," (Changes your selected group's translation and not it's actual location, use \"move\" instead if this group uses animations)");
        CMDUtils.sendCMD(sender, "/mdis group movehere", " (Change your selected group's actual location to your location)");
        CMDUtils.sendCMD(sender, "/mdis group merge <distance>"," (Merges groups with your selected group)");
        CMDUtils.sendCMD(sender, "/mdis group copypose", " (Copies the transformations of the group you're closest to, to your selected group)");
        CMDUtils.sendCMD(sender, "/mdis group glow", " (Make all parts in this group glow)");
        CMDUtils.sendCMD(sender, "/mdis group setglowcolor <color | hex-code>", "(Set the glow color for all parts in this group)");
    }
    
}
