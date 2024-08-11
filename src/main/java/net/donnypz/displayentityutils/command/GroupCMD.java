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
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group selectnearest <interaction-distance>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group info");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group spawn <group-tag> <storage>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group despawn");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group save <storage-location>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group delete <group-tag> <storage-location>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group addtarget", " (Add a targeted interaction entity to your group)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group removeinteractions");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group settag <group-tag>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group setyaw <yaw> [-pivot]"," (Put \"-pivot\" at the end of the command to pivot interaction entities)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group setpitch <pitch>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group clone", " (Spawn a cloned group at your selected group's location)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group clonehere", " (Spawn a cloned group at your location)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group move <direction> <distance> <tick-duration>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group translate <direction> <distance> <tick-duration>"," (Changes this group's translation and not it's actual location, use \"move\" instead if this group uses animations)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group scale <scale-multiplier> <tick-duration>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group movehere");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group merge"," (Merges groups at the same position)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group copypose", " (Copies transformation of one group to another, despawning the copied group)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group glow");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis group setglowcolor <color | hex-code>");
    }
    
}
