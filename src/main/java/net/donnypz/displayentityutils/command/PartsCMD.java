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

class PartsCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    PartsCMD(){
        subCommands.put("help", new PartsHelpCMD());
        subCommands.put("cycle", new PartsCycleCMD());
        subCommands.put("glow", new PartsGlowCMD());
        subCommands.put("setglowcolor", new PartsSetGlowColorCMD());
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
            sender.sendMessage(Component.text("\"Parts\" are each individual display/interaction entity that is spawned within the group", NamedTextColor.AQUA));
            sender.sendMessage(Component.text("Add tags to parts to identify each part in a group", NamedTextColor.AQUA));
            sender.sendMessage(Component.text(" | Mainly useful for API users, creating animations, and usage with addon plugins", NamedTextColor.GRAY));
            CMDUtils.sendCMD(sender, "/mdis parts help <page-number>", " (Get help for parats)");
            CMDUtils.sendCMD(sender, "/mdis parts cycle <first | prev | next>", " (Cycle between selected parts or all parts in your group)");
            CMDUtils.sendCMD(sender, "/mdis parts addtag <part-tag>", " (Add a tag to a part)");
            CMDUtils.sendCMD(sender, "/mdis parts removetag <part-tag>", " (Remove a tag from a part)");
            CMDUtils.sendCMD(sender, "/mdis parts adapttags <part-tag> [-remove]",
                    " (Adapt existing scoreboard tags to tags usable by DisplayEntityUtils. Done on selected parts or the group if there's no selection."+
                            " \"-remove\" removes the tag from the scoreboard)");
        }
        else{
            CMDUtils.sendCMD(sender, "/mdis parts listtags", " (List all tags a part has)");
            CMDUtils.sendCMD(sender, "/mdis parts select <part-tag>", " (Select multiple parts by a part tag they contain)");
            CMDUtils.sendCMD(sender, "/mdis parts deselect", " (Clear your part selection)");
            CMDUtils.sendCMD(sender, "/mdis parts remove", " (Despawn and remove a part from a group)");
            CMDUtils.sendCMD(sender, "/mdis parts glow", " (Make selected parts glow temporarily)");
            CMDUtils.sendCMD(sender, "/mdis parts setglowcolor <color | hex-code>", " (Set the glow color for selected parts)");
            CMDUtils.sendCMD(sender, "/mdis parts translate <direction> <distance> <tick-duration>", " (Translate a selected part)");
            CMDUtils.sendCMD(sender, "/mdis parts seeduuids <group | selection> <seed>"," (Useful when wanting to use the same animation on similar groups)");
            CMDUtils.sendCMD(sender, "/mdis parts setblock <\"-held\" | \"-target\" | block-id>", " (Easily change the block of a block display part)");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static void noPartSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You have not selected a part!");
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next>", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/mdis parts select <part-tag>", NamedTextColor.GRAY));
    }


}
