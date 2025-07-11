package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class AnimCMD extends ConsoleUsableSubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    AnimCMD(){
        super(Permission.HELP);
        subCommands.put("help", new AnimHelpCMD());
        subCommands.put("new", new AnimNewCMD());
        subCommands.put("save", new AnimSaveCMD());
        subCommands.put("delete", new AnimDeleteCMD());
        subCommands.put("info", new AnimInfoCMD());
        subCommands.put("frameinfo", new AnimFrameInfoCMD());
        subCommands.put("usefilter", new AnimUseFilterCMD());
        subCommands.put("unfilter", new AnimUnfilterCMD());
        subCommands.put("addframe", new AnimAddFrameCMD());
        subCommands.put("addframeafter", new AnimAddFrameAfterCMD());
        subCommands.put("removeframe", new AnimRemoveFrameCMD());
        subCommands.put("overwriteframe", new AnimOverwriteFrameCMD());
        subCommands.put("editframe", new AnimEditFrameCMD());
        subCommands.put("editallframes", new AnimEditAllFramesCMD());
        subCommands.put("addpoint", new AnimAddPointCMD());
        subCommands.put("drawpoints", new AnimDrawPointsCMD());
        subCommands.put("drawpos", new AnimDrawPosCMD());
        subCommands.put("copypoint", new AnimCopyPointCMD());
        subCommands.put("movepoint", new AnimMovePointCMD());
        subCommands.put("showframe", new AnimShowFrameCMD());
        subCommands.put("addsound", new AnimAddSoundCMD());
        subCommands.put("removesound", new AnimRemoveSoundCMD());
        subCommands.put("addparticle", new AnimAddParticleCMD());
        subCommands.put("cancelpoints", new AnimCancelPointsCMD());
        subCommands.put("reverse", new AnimReverseCMD());
        subCommands.put("togglescalerespect", new AnimScaleRespectCMD());
        subCommands.put("toggledatachanges", new AnimDataChangesCMD());
        subCommands.put("settag", new AnimSetTagCMD());
        subCommands.put("setframetag", new AnimSetFrameTagCMD());
        subCommands.put("previewplay", new AnimPreviewPlayCMD());
        subCommands.put("play", new AnimPlayCMD());
        subCommands.put("stop", new AnimStopCMD());
        subCommands.put("select", new AnimSelectCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            animationHelp(sender, 1);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.HELP)){
                return;
            }
            animationHelp(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void animationHelp(CommandSender sender, int page){
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        if (page <= 1){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Convert animations from <yellow>block-display.com <aqua>with " +
                    "\"/mdis bdengine convertanim\""));
            sender.sendMessage(Component.text("Commands allowing multiple <frame-ids> are comma separated", NamedTextColor.GRAY));
            CMDUtils.sendCMD(sender,"/mdis anim help <page-number>", "Get help for animations");
            CMDUtils.sendCMD(sender,"/mdis anim new", "Manually create a new animation");
            CMDUtils.sendCMD(sender, "/mdis anim select <anim-tag> <storage-location>", "Select a saved animation");
            CMDUtils.sendCMD(sender,"/mdis anim info", "List information about your selected animation");
            CMDUtils.sendCMD(sender, "/mdis anim frameinfo <frame-id>", "List information about a frame in your animation");
            CMDUtils.sendCMD(sender, "/mdis anim settag", "Set the tag to identify this animation");
        }
        else if (page == 2){
            CMDUtils.sendCMD(sender, "/mdis anim setframetag <frame-ids> <frame-tag>", "Set the tag to identify a frame");
            CMDUtils.sendCMD(sender, "/mdis anim usefilter [-trim]", "Apply your part filter/selection to an animation. Optionally, and irreversibly remove data of unfiltered parts");
            CMDUtils.sendCMD(sender, "/mdis anim unfilter", "Remove your part filter/selection from an animation");
            CMDUtils.sendCMD(sender, "/mdis anim addframe <tick-delay> <tick-duration>", "Add a frame to your selected animation");
            CMDUtils.sendCMD(sender, "/mdis anim addframeafter <frame-id> <tick-delay> <tick-duration>", "Add a frame after another frame to your selected animation");
            CMDUtils.sendCMD(sender, "/mdis anim removeframe <frame-id>", "Remove a frame from your selected animation");
            CMDUtils.sendCMD(sender, "/mdis anim overwriteframe <frame-id>", "Overwrite the transformation data of a frame");
        }
        else if (page == 3){
            CMDUtils.sendCMD(sender, "/mdis anim editframe <frame-ids | frame-tag> <tick-delay> <tick-duration>", "Edit properties of a frame");
            CMDUtils.sendCMD(sender, "/mdis anim editallframes <tick-delay> <tick-duration>", "Edit properties of all frames");
            CMDUtils.sendCMD(sender, "/mdis anim addpoint <frame-id> <point-tag>", "Add a point relative to a group and your location for a frame)");
            CMDUtils.sendUnsafeCMD(sender, "/mdis anim drawpos <1 | 2 | 3>", "Set where frame points should be drawn between. Set 3 when drawing arcs");
            CMDUtils.sendUnsafeCMD(sender, "/mdis anim drawpoints <straight | arc> <point-tag> <start-frame> <end-frame> [points-per-frame]", "Draw a straight/arc'd line of frame points between frames");
            CMDUtils.sendCMD(sender, "/mdis anim copypoint <frame-ids | frame-tag>", "Copy a selected frame point to other frames");
            CMDUtils.sendCMD(sender, "/mdis anim movepoint", "Move a frame point to your location, relative to your selected group");
        }
        else if (page == 4){
            CMDUtils.sendCMD(sender, "/mdis anim addsound <sound> <volume> <pitch>", "Add a sound to play at a frame point");
            CMDUtils.sendCMD(sender, "/mdis anim removesound <sound | -all>", "Remove a sound from a frame point");
            CMDUtils.sendCMD(sender, "/mdis anim addparticle", "Add a particle to play at a frame point");
            CMDUtils.sendCMD(sender, "/mdis anim reverse", "Reverse the order of frames in your selected animation");
            CMDUtils.sendCMD(sender, "/mdis anim togglescalerespect", "Toggle whether your selected animation should respect the group's scale");
            CMDUtils.sendCMD(sender, "/mdis anim toggledatachanges", "Toggle whether your selected animation can change block display blocks, item display items, and text display text");
            CMDUtils.sendCMD(sender, "/mdis anim showframe <frame-id>", "Displays a frame on your selected group");
        }
        else{
            CMDUtils.sendCMD(sender, "/mdis anim play [-loop]", "Play your selected animation on your selected group. Include \"-loop\" to loop the animation");
            CMDUtils.sendCMD(sender, "/mdis anim previewplay", "Preview your selected animation on your selected group, with only you seeing the animation.");
            CMDUtils.sendCMD(sender, "/mdis anim stop", "Stop an animation playing on a group");
            CMDUtils.sendCMD(sender, "/mdis anim save <storage-location>", "Save your selected animation and any changes made");
            CMDUtils.sendCMD(sender, "/mdis anim delete <anim-tag> <storage-location>", "Delete a saved animation");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static void noAnimationSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You do not have an animation selected!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis anim select <animation-tag>", NamedTextColor.GRAY));
    }

    static void noFramePointSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You do not have an frame point selected!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis anim frameinfo <frame-id>", NamedTextColor.GRAY));
    }

    static void hasNoFrames(Player player){
        player.sendMessage(Component.text("Your currently selected animation has no frames!", NamedTextColor.RED));
        player.sendMessage(Component.text("Use \"/mdis anim addframe\" instead", NamedTextColor.GRAY));
    }

}
