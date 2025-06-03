package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class PartsCMD extends ConsoleUsableSubCommand {

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    PartsCMD(){
        super(Permission.HELP);
        subCommands.put("help", new PartsHelpCMD());
        subCommands.put("info", new PartsInfoCMD());
        subCommands.put("cycle", new PartsCycleCMD());
        subCommands.put("glow", new PartsGlowCMD());
        subCommands.put("unglow", new PartsUnglowCMD());
        subCommands.put("glowcolor", new PartsGlowColorCMD());
        subCommands.put("filtertags", new PartsFilterTagsCMD());
        subCommands.put("filtertypes", new PartsFilterTypesCMD());
        subCommands.put("filterblocks", new PartsFilterBlocksCMD());
        subCommands.put("filteritems", new PartsFilterItemsCMD());
        subCommands.put("refresh", new PartsRefreshCMD());
        subCommands.put("reset", new PartsResetCMD());
        subCommands.put("adapttags", new PartsAdaptTagsCMD());
        subCommands.put("addtag", new PartsAddTagCMD());
        subCommands.put("removetag", new PartsRemoveTagCMD());
        subCommands.put("listtags", new PartsListTagsCMD());
        subCommands.put("remove", new PartsRemoveCMD());
        subCommands.put("translate", new PartsTranslateCMD());
        subCommands.put("seeduuids", new PartsSeedUUIDsCMD());
        subCommands.put("setblock", new PartsSetBlockCMD());
        subCommands.put("billboard", new PartsBillboardCMD());
        subCommands.put("viewrange", new PartsViewRangeCMD());
        subCommands.put("brightness", new PartsBrightnessCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            partsHelp(sender, 1);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            partsHelp(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
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
            CMDUtils.sendCMD(sender, "/mdis parts help <page-number>", "Get help for parts");
            CMDUtils.sendCMD(sender, "/mdis parts info", "Get information about your current part/selection");
            CMDUtils.sendCMD(sender, "/mdis parts cycle <first | prev | next | last> [jump]", "Cycle between parts in your part selection");
            CMDUtils.sendCMD(sender, "/mdis parts addtag <part-tag> [-all]", "Add a tag to your selected part");
            CMDUtils.sendCMD(sender, "/mdis parts removetag <part-tag> [-all]", "Remove a tag from your selected part");
            CMDUtils.sendCMD(sender, "/mdis parts adapttags [-remove]",
                    "Adapt scoreboard tags to tags usable by DisplayEntityUtils. Applied to selected parts."+
                            " \"-remove\" removes tag from scoreboard");
        }
        else if (page == 2) {
            CMDUtils.sendCMD(sender, "/mdis parts listtags ", "List tags of the currently selected part");
            CMDUtils.sendCMD(sender, "/mdis parts filtertags <part-tags>", "Filter parts by part tags, comma separated. Exclude A tag by prefixing it with \"!\"");
            CMDUtils.sendCMD(sender, "/mdis parts filtertypes <part-types>", "Filter parts by their type. Exclude ALL filtered types by prefixing with \"!\"");
            CMDUtils.sendCMD(sender, "/mdis parts filterblocks <block-ids>", "Filter blocks of BLOCK parts. Exclude ALL filtered blocks by prefixing with \"!\"");
            CMDUtils.sendCMD(sender, "/mdis parts filteritems <item-ids>", "Filter items of ITEM parts. Exclude ALL filtered items by prefixing with \"!\"");
            CMDUtils.sendCMD(sender, "/mdis parts refresh", "Refresh your part selection after making some type of change");
            CMDUtils.sendCMD(sender, "/mdis parts reset", "Reset your part selection and any filters");
            CMDUtils.sendCMD(sender, "/mdis parts remove [-all]", "Despawn and remove your selected part from a group");
        }
        else if (page == 3){
            CMDUtils.sendCMD(sender, "/mdis parts glow [-all]", "Make your selected part glow");
            CMDUtils.sendCMD(sender, "/mdis parts unglow [-all]", "Remove the glow from your selected part");
            CMDUtils.sendCMD(sender, "/mdis parts glowcolor <color | hex-code> [-all]", "Set your selected part's glow color");
            CMDUtils.sendCMD(sender, "/mdis parts brightness <block> <sky> [-all]", "Set your selected part's brightness. Enter values between 0-15. -1 resets");
            CMDUtils.sendCMD(sender, "/mdis parts viewrange <view-range-multiplier> [-all]", "Set the view range multiplier for your selected part");
            CMDUtils.sendCMD(sender, "/mdis parts billboard <fixed | vertical | horizontal | center> [-all]", "Set the billboard of your selected part");
            CMDUtils.sendCMD(sender, "/mdis parts translate <direction> <distance> <tick-duration> [-all]", "Translate your selected part");
        }
        else{
            CMDUtils.sendCMD(sender, "/mdis parts setblock <\"-held\" | \"-target\" | block-id> [-all]", "Change the block of a block display part");
            CMDUtils.sendCMD(sender, "/mdis parts seeduuids <group | selection> <seed>","Useful when wanting to use the same animation on similar groups");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static void noPartSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You have not selected any part(s)!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next>", NamedTextColor.GRAY));
    }

    static void invalidPartSelection(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your part selection is invalid!", NamedTextColor.RED)));
    }


}
