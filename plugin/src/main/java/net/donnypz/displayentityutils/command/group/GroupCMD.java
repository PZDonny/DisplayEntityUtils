package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class GroupCMD extends ConsoleUsableSubCommand {

    public GroupCMD(){
        super(Permission.HELP, new GroupHelpCMD());
        this.subCommands.put("list", new ListCMD(
                Component.text("Incorrect Usage! /deu group list <storage> [page-number]", NamedTextColor.RED),
                3,
                true));
        new GroupSelectCMD(this);
        new GroupSelectNearestCMD(this);
        new GroupDeselectCMD(this);
        new GroupSaveCMD(this);
        new GroupSaveJsonCMD(this);
        new GroupToPacketCMD(this);
        new GroupMarkPacketGroupsCMD(this);
        new GroupHidePersistentPacketGroupsCMD(this);
        new GroupShowPersistentPacketGroupsCMD(this);
        new GroupDeleteCMD(this);
        new GroupSpawnCMD(this);
        new GroupSpawnJSONCMD(this);
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
        new GroupSetSpawnAnimationCMD(this);
        new GroupUnsetSpawnAnimationCMD(this);
        new GroupPersistCMD(this);
        new GroupPersistenceOverrideCMD(this);
        new GroupBillboardCMD(this);
        new GroupViewRangeCMD(this);
        new GroupRideCMD(this);
        new GroupSafeDismountCMD(this);
        new GroupDismountCMD(this);
        new GroupWorldEditCMD(this);
        new GroupAutoCullCMD(this);
        new GroupRemoveCullCMD(this);
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

    static void groupToPacketInfo(Player player){
        player.sendMessage(Component.text("| Selected groups can become packet-based with \"/deu group topacket\"", NamedTextColor.GRAY, TextDecoration.ITALIC));
    }


    static void groupHelp(CommandSender sender, int page){
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        if (page <= 1){
            CMDUtils.sendCMD(sender, "/deu group help <page-number>", "Get help for groups");
            CMDUtils.sendCMD(sender, "/deu anim list <storage> [page-number]", "List all saved display entity groups/models");
            CMDUtils.sendCMD(sender, "/deu group select <distance>", "Select from nearby groups within the given distance");
            CMDUtils.sendCMD(sender, "/deu group selectnearest <distance>", "Select the nearest group within the given distance");
            CMDUtils.sendCMD(sender, "/deu group deselect", "Clear your group selection");
            CMDUtils.sendCMD(sender, "/deu group info", "List information about your selected group");
            CMDUtils.sendCMD(sender, "/deu group spawn <group-tag> <storage> [-packet]", "Spawn a saved display entity group/model from a storage location. \"-packet\" will spawn the group/model using packets");
        }
        else if (page == 2) {
            CMDUtils.sendCMD(sender, "/deu group spawnjson <group-tag> [-packet]", "Spawn a JSON saved display entity group/model from a local storage. \"-packet\" will spawn the group/model using packets");
            CMDUtils.sendCMD(sender, "/deu group despawn", "Despawn your selected group");
            CMDUtils.sendCMD(sender, "/deu group save <storage>", "Save your selected group");
            CMDUtils.sendUnsafeCMD(sender, "/deu group savejson", "Save your selected group as a JSON file. Spawning groups from JSON files will always be slower");
            CMDUtils.sendCMD(sender, "/deu group delete <group-tag> <storage>", "Delete a saved group from a storage location");
            CMDUtils.sendCMD(sender, "/deu group topacket [-confirm] [-keep]", "Make your selected group packet-based, making it unselectable. \"-confirm\" confirms the action." +
                    " \"-keep\" keeps the non-packet based version of your group spawned.");
            CMDUtils.sendCMD(sender, "/deu group markpacketgroups", "Create markers for all packet groups stored in your current chunk");
        }
        else if (page == 3){
            CMDUtils.sendCMD(sender, "/deu group showpacketgroups [-self]", "Show all persistent packet-based groups in your current chunk. \n\"-self\" shows the group only for you");
            CMDUtils.sendCMD(sender, "/deu group hidepacketgroups [-self]", "Hide all persistent packet-based groups in your current chunk. \n\"-self\" hides the group only for you");
            CMDUtils.sendCMD(sender, "/deu group addtarget", "Add a targeted interaction entity to your group");
            CMDUtils.sendCMD(sender, "/deu group ungroupinteractions", "Remove all interactions from your group");
            CMDUtils.sendCMD(sender, "/deu group settag <group-tag>", "Set this group's tag, or identifier");
            CMDUtils.sendCMD(sender, "/deu group yaw <yaw> [-pivot]","Set your selected group's yaw, \"-pivot\" pivots interaction entities around the group");
            CMDUtils.sendCMD(sender, "/deu group pitch <pitch>", "Set your selected group's pitch");
        }
        else if (page == 4){
            CMDUtils.sendCMD(sender, "/deu group scale <scale-multiplier> <tick-duration>", "Scale your selected group with a given multiplier");
            CMDUtils.sendCMD(sender, "/deu group brightness <block> <sky>", "Set your selected group's brightness. Enter values between 0-15. -1 resets");
            CMDUtils.sendCMD(sender, "/deu group clone", "Spawn a cloned group at your selected group's location");
            CMDUtils.sendCMD(sender, "/deu group clonehere", "Spawn a cloned group at your location");
            CMDUtils.sendCMD(sender, "/deu group move <direction> <distance> [tick-duration]", "Change the actual location of your selected group, with an optional duration");
            CMDUtils.sendCMD(sender, "/deu group movehere", "Change your selected group's actual location to your location");
            CMDUtils.sendCMD(sender, "/deu group translate <direction> <distance> <tick-duration>","Changes your selected group's translation, use \"move\" instead if this group uses animations");
        }
        else if (page == 5){
            CMDUtils.sendCMD(sender, "/deu group merge <distance>","Merge groups near your selected group");
            CMDUtils.sendCMD(sender, "/deu group billboard <fixed | vertical | horizontal | center>", "Set the billboard of all parts in this group");
            CMDUtils.sendCMD(sender, "/deu group glowcolor <color | hex-code>", "Set the glow color for all parts in this group");
            CMDUtils.sendCMD(sender, "/deu group glow", "Make all parts in this group glow");
            CMDUtils.sendCMD(sender, "/deu group unglow", "Remove the glowing effect from all parts in this group");
            CMDUtils.sendCMD(sender, "/deu group ride <-target | player-name | entity-uuid> [group-tag] [storage] [controller-id]", "Make a group ride an entity. Values in brackets [] are optional");
            CMDUtils.sendCMD(sender, "/deu group safedismount <-target | -selected | player-name | entity-uuid>", "Safely dismount a group from an entity");
        }
        else if (page == 6){
            CMDUtils.sendCMD(sender, "/deu group dismount <-target | -selected | player-name | entity-uuid> [-despawn]", "Dismount a group from an entity, with optional despawning");
            CMDUtils.sendCMD(sender, "/deu group setspawnanim <anim-tag> <storage> <linear | loop>", "Set an animation to play when this group is spawned/loaded");
            CMDUtils.sendCMD(sender, "/deu group unsetspawnanim", "Remove the spawn animation that's set on your selected group");
            CMDUtils.sendCMD(sender, "/deu group viewrange <view-range-multiplier>", "Set the view range multiplier for your selected group");
            CMDUtils.sendCMD(sender, "/deu group autocull", "Calculate and set culling bounds for every part in your selected group");
            CMDUtils.sendCMD(sender, "/deu group removecull", "Remove the culling bounds for every part in your selected group");
            CMDUtils.sendCMD(sender, "/deu group togglepersist", "Toggle if your group should persist after a server shutdown");
        }
        else{
            CMDUtils.sendCMD(sender, "/deu group togglepersistoverride", "Toggle if your group's persistence can be overriden when loaded by a chunk, " +
                    "only if \"persistenceOverride\" is enabled in the config");
            CMDUtils.sendCMD(sender, "/deu group wetogroup [-removeblocks]", "Convert your WorldEdit selection to a display group, with its origin at the lowest center block of the selection (Requires WorldEdit). \nUse \"-removeblocks\" to set all selected blocks to air");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }
    
}
