package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AnimCMD extends ConsoleUsableSubCommand {

    public AnimCMD(){
        super(Permission.HELP, new AnimHelpCMD());
        new AnimNewCMD(this);
        new AnimSaveCMD(this);
        new AnimSaveJsonCMD(this);
        new AnimDeleteCMD(this);
        new AnimInfoCMD(this);
        new AnimFrameInfoCMD(this);
        new AnimUseFilterCMD(this);
        new AnimUnfilterCMD(this);
        new AnimAddFrameCMD(this);
        new AnimAddFrameAfterCMD(this);
        new AnimRemoveFrameCMD(this);
        new AnimOverwriteFrameCMD(this);
        new AnimEditFrameCMD(this);
        new AnimEditAllFramesCMD(this);
        new AnimAddPointCMD(this);
        new AnimDrawPointsCMD(this);
        new AnimDrawPosCMD(this);
        new AnimCopyPointCMD(this);
        new AnimMovePointCMD(this);
        new AnimShowFrameCMD(this);
        new AnimPreviewFrameCMD(this);
        new AnimAddSoundCMD(this);
        new AnimRemoveSoundCMD(this);
        new AnimAddParticleCMD(this);
        new AnimReverseCMD(this);
        new AnimScaleRespectCMD(this);
        new AnimTextureChangesCMD(this);
        new AnimSetTagCMD(this);
        new AnimSetFrameTagCMD(this);
        new AnimPreviewPlayCMD(this);
        new AnimPlayCMD(this);
        new AnimStopCMD(this);
        new AnimRestoreCMD(this);
        new AnimSelectCMD(this);
        new AnimSelectJSONCMD(this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            animationHelp(sender, 1);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
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
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        if (page <= 1){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Convert animations from <yellow>block-display.com <aqua>with " +
                    "\"/deu bdengine convertanim\""));
            sender.sendMessage(Component.text("Commands allowing multiple <frame-ids> are comma separated", NamedTextColor.GRAY));
            CMDUtils.sendCMD(sender,"/deu anim help <page-number>", "Get help for animations");
            CMDUtils.sendCMD(sender,"/deu anim new", "Manually create a new animation");
            CMDUtils.sendCMD(sender, "/deu anim select <anim-tag> <storage>", "Select a saved animation");
            CMDUtils.sendCMD(sender, "/deu anim selectjson <file-name>", "Select a JSON saved animation");
            CMDUtils.sendCMD(sender,"/deu anim info", "List information about your selected animation");
            CMDUtils.sendCMD(sender, "/deu anim frameinfo <frame-id>", "List information about a frame in your animation");
        }
        else if (page == 2){
            CMDUtils.sendCMD(sender, "/deu anim settag", "Set the tag to identify this animation");
            CMDUtils.sendCMD(sender, "/deu anim setframetag <frame-ids> <frame-tag>", "Set the tag to identify a frame");
            CMDUtils.sendCMD(sender, "/deu anim usefilter [-trim]", "Apply your part filter/selection to an animation. Optionally, and irreversibly remove data of unfiltered parts");
            CMDUtils.sendCMD(sender, "/deu anim unfilter", "Remove your part filter/selection from an animation");
            CMDUtils.sendCMD(sender, "/deu anim addframe <tick-delay> <tick-duration>", "Add a frame to your selected animation");
            CMDUtils.sendCMD(sender, "/deu anim addframeafter <frame-id> <tick-delay> <tick-duration>", "Add a frame after another frame to your selected animation");
            CMDUtils.sendCMD(sender, "/deu anim removeframe <frame-id>", "Remove a frame from your selected animation");
        }
        else if (page == 3){
            CMDUtils.sendCMD(sender, "/deu anim overwriteframe <frame-id>", "Overwrite the transformation data of a frame");
            CMDUtils.sendCMD(sender, "/deu anim editframe <frame-ids | frame-tag> <tick-delay> <tick-duration>", "Edit properties of a frame");
            CMDUtils.sendCMD(sender, "/deu anim editallframes <tick-delay> <tick-duration>", "Edit properties of all frames");
            CMDUtils.sendCMD(sender, "/deu anim addpoint <frame-id> <point-tag>", "Add a point relative to a group and your location for a frame)");
            CMDUtils.sendUnsafeCMD(sender, "/deu anim drawpos <1 | 2 | 3>", "Set where frame points should be drawn between. Set 3 when drawing arcs");
            CMDUtils.sendUnsafeCMD(sender, "/deu anim drawpoints <straight | arc> <point-tag> <start-frame> <end-frame> [points-per-frame]", "Draw a straight/arc'd line of frame points between frames");
            CMDUtils.sendCMD(sender, "/deu anim copypoint <frame-ids | frame-tag>", "Copy a selected frame point to other frames");
        }
        else if (page == 4){
            CMDUtils.sendCMD(sender, "/deu anim movepoint", "Move a frame point to your location, relative to your selected group");
            CMDUtils.sendCMD(sender, "/deu anim addsound <sound> <volume> <pitch>", "Add a sound to play at a frame point");
            CMDUtils.sendCMD(sender, "/deu anim removesound <sound | -all>", "Remove a sound from a frame point");
            CMDUtils.sendCMD(sender, "/deu anim addparticle", "Add a particle to play at a frame point");
            CMDUtils.sendCMD(sender, "/deu anim reverse", "Reverse the order of frames in your selected animation");
            CMDUtils.sendCMD(sender, "/deu anim togglescalerespect", "Toggle whether your selected animation should respect the group's scale");
            CMDUtils.sendCMD(sender, "/deu anim toggletexturechanges", "Toggle whether your selected animation can change block display blocks, item display items, and text display text");
        }
        else if (page == 5){
            CMDUtils.sendCMD(sender, "/deu anim showframe <frame-id>", "Displays a frame on your selected group");
            CMDUtils.sendCMD(sender, "/deu anim previewframe <frame-id>", "Preview a frame on your selected group, without changing group entity data");
            CMDUtils.sendCMD(sender, "/deu anim play [-loop] [-packet] [-camera]", "Play your selected animation on your selected group." +
                    " \n\"-loop\" will make the animation loop." +
                    " \n\"-packet\" will play the animation using packets." +
                    " \n\"-camera\" will set your view to the animation's camera, if present");
            CMDUtils.sendCMD(sender, "/deu anim previewplay [-camera]", "Preview your selected animation on your selected group, without changing group entity data." +
                    " \n\"-camera\" will set your view to the animation's camera, if present");
            CMDUtils.sendCMD(sender, "/deu anim restore", "Restore your selected group to its previous state before previewing frames/animations");
            CMDUtils.sendCMD(sender, "/deu anim stop", "Stop an animation playing on a group");
            CMDUtils.sendCMD(sender, "/deu anim save <storage>", "Save your selected animation and any changes made");
        }
        else{
            CMDUtils.sendUnsafeCMD(sender, "/deu anim savejson", "Save your selected animation and any changes made as a JSON file. Selection animations from JSON files will always be slower");
            CMDUtils.sendCMD(sender, "/deu anim delete <anim-tag> <storage>", "Delete a saved animation");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static void noAnimationSelection(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You do not have an animation selected!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu anim select <animation-tag>", NamedTextColor.GRAY));
    }

    static void noFramePointSelection(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You do not have an frame point selected!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu anim frameinfo <frame-id>", NamedTextColor.GRAY));
    }

    static void hasNoFrames(Player player){
        player.sendMessage(Component.text("Your currently selected animation has no frames!", NamedTextColor.RED));
        player.sendMessage(Component.text("Use \"/deu anim addframe\" instead", NamedTextColor.GRAY));
    }
}