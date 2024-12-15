package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class PartsCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    PartsCMD(){
        subCommands.put("help", new PartsHelpCMD());
        subCommands.put("cycle", new PartsCycleCMD());
        subCommands.put("glow", new PartsGlowCMD());
        subCommands.put("glowcolor", new PartsGlowColorCMD());
        subCommands.put("select", new PartsSelectCMD());
        subCommands.put("deselect", new PartsDeselectCMD());
        subCommands.put("adapttags", new PartsAdaptTagsCMD());
        subCommands.put("addtag", new PartsAddTagCMD());
        subCommands.put("removetag", new PartsRemoveTagCMD());
        subCommands.put("listtags", new PartsListTagsCMD());
        subCommands.put("remove", new PartsRemoveCMD());
        subCommands.put("translate", new PartsTranslateCMD());
        subCommands.put("seeduuids", new PartsSeedUUIDsCMD());
        subCommands.put("setblock", new PartsSetBlockCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2){
            partsHelp(player, 1);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
                return;
            }
            partsHelp(player, 1);
        }
        else{
            subCommand.execute(player, args);
        }
    }

    static void partsHelp(CommandSender sender, int page){
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        if (page <= 1){
            sender.sendMessage(Component.text("\"Parts\" are each individual display/interaction entity that is contained within a group", NamedTextColor.AQUA));
            sender.sendMessage(Component.text("| Add tags to parts to identify each part in a group", NamedTextColor.AQUA));
            sender.sendMessage(Component.text("| \"-all\" will apply the command to all parts within your part selection where valid. By default a selected group's parts is your part selection", NamedTextColor.GOLD));
            sender.sendMessage(Component.empty());
            CMDUtils.sendCMD(sender, "/mdis parts help <page-number>", " (Get help for parts)");
            CMDUtils.sendCMD(sender, "/mdis parts cycle <first | prev | next | last> [jump]", " (Cycle between parts in your part selection)");
            CMDUtils.sendCMD(sender, "/mdis parts addtag <part-tag> [-all]", " (Add a tag to your selected part");
            CMDUtils.sendCMD(sender, "/mdis parts removetag <part-tag> [-all]", " (Remove a tag from your selected part)");
            CMDUtils.sendCMD(sender, "/mdis parts adapttags [-remove]",
                    " (Adapt scoreboard tags to tags usable by DisplayEntityUtils. Done to selected parts or group if there's no selection."+
                            " \"-remove\" removes tag from scoreboard)");
        }
        else{
            CMDUtils.sendCMD(sender, "/mdis parts listtags <part | selection>", " (List tags of selected part or entire part selection)");
            CMDUtils.sendCMD(sender, "/mdis parts select <part-tag>", " (Select multiple parts by their part tag)");
            CMDUtils.sendCMD(sender, "/mdis parts deselect", " (Clear your part selection)");
            CMDUtils.sendCMD(sender, "/mdis parts remove [-all]", " (Despawn and remove your selected part from a group)");
            CMDUtils.sendCMD(sender, "/mdis parts glow [-toggle] [-all]", " (Make a part or all selected parts glow. \"-toggle\" to toggle the glowing)");
            CMDUtils.sendCMD(sender, "/mdis parts glowcolor <color | hex-code> [-all]", " (Set your selected part's glow color)");
            CMDUtils.sendCMD(sender, "/mdis parts translate <direction> <distance> <tick-duration> [-all]", " (Translate your selected part)");
            CMDUtils.sendCMD(sender, "/mdis parts seeduuids <group | selection> <seed>"," (Useful when wanting to use the same animation on similar groups.)");
            CMDUtils.sendCMD(sender, "/mdis parts setblock <\"-held\" | \"-target\" | block-id> [-all]", " (Change the block of a block display part)");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static void noPartSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You have not selected a part!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next>", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/mdis parts select <part-tag>", NamedTextColor.GRAY));
    }

    static void invalidPartSelection(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your part selection is invalid!", NamedTextColor.RED)));
    }


}
