package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class AnimCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    AnimCMD(){
        subCommands.put("new", new AnimNewCMD());
        subCommands.put("save", new AnimSaveCMD());
        subCommands.put("delete", new AnimDeleteCMD());
        subCommands.put("info", new AnimInfoCMD());
        subCommands.put("frameinfo", new AnimFrameInfoCMD());
        subCommands.put("addframe", new AnimAddFrameCMD());
        subCommands.put("addframeafter", new AnimAddFrameAfterCMD());
        subCommands.put("removeframe", new AnimRemoveFrameCMD());
        subCommands.put("overwriteframe", new AnimOverwriteFrameCMD());
        subCommands.put("editframe", new AnimEditFrameCMD());
        subCommands.put("editallframes", new AnimEditAllFramesCMD());
        subCommands.put("showframe", new AnimShowFrameCMD());
        subCommands.put("addsound", new AnimAddSoundCMD());
        subCommands.put("removesound", new AnimRemoveSoundCMD());
        subCommands.put("reverse", new AnimReverseCMD());
        subCommands.put("togglescalerespect", new AnimScaleRespectCMD());
        subCommands.put("settag", new AnimSetTagCMD());
        subCommands.put("setframetag", new AnimSetFrameTagCMD());
        subCommands.put("play", new AnimPlayCMD());
        subCommands.put("select", new AnimSelectCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2){
            animationHelp(player);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
                return;
            }
            animationHelp(player);
        }
        else{
            subCommand.execute(player, args);
        }
    }

    static void animationHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(Component.text("Create animations that can be saved to a DisplayEntityGroup", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("If using animations, use \"move\" on groups instead of \"translate\"", NamedTextColor.AQUA));
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Use <yellow>\"block-display.com\" <aqua>to easier create animations and convert them "));
        sender.sendMessage(Component.text("Use "+ ChatColor.YELLOW+"\"block-display.com\""+ChatColor.AQUA+" to create models and keyframes", NamedTextColor.AQUA));
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis anim new [part-tag] ", "(Animates only parts with a tag, if the tag is specified)");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis anim info");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim frameinfo <frame-id>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim addframe <delay-in-ticks> <duration-in-ticks>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim addframeafter <frame-id> <delay-in-ticks> <duration-in-ticks>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim removeframe <frame-id>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim showframe <frame-id>", " (Displays a frame on the group)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim editframe <frame-id> <delay-in-ticks> <duration-in-ticks>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim editallframes <delay-in-ticks> <duration-in-ticks>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim addsound <frame-id> <sound> <volume> <pitch> <start | end>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim removesound <frame-id> <sound> <start | end>");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim overwriteframe <frame-id>", " (Overwrite the transformation data of a frame)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim reverse", "(Reverse the order of frames in this animation)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim reset");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim togglescalerespect", " (Toggle whether this animation should respect the group's scale)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim play");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis anim select <anim-tag>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis anim save <storage-location>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis anim delete <anim-tag> <storage-location>");

    }

    static void noAnimationSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You have not have an animation selected!");
        player.sendMessage(Component.text("/mdis anim select <animation-tag>", NamedTextColor.GRAY));
    }

    static void hasNoFrames(Player player){
        player.sendMessage(Component.text("Your currently selected animation has no frames!", NamedTextColor.RED));
        player.sendMessage(Component.text("Use \"/mdis anim addframe\" instead", NamedTextColor.GRAY));
    }

}
