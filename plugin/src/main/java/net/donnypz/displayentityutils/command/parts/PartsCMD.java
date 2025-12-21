package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PartsCMD extends ConsoleUsableSubCommand {

    public PartsCMD(){
        super(Permission.HELP, new PartsHelpCMD());
        new PartsInfoCMD(this);
        new PartsCreateCMD(this);
        new PartsSelectCMD(this);
        new PartsCycleCMD(this);
        new PartsGlowCMD(this);
        new PartsUnglowCMD(this);
        new PartsGlowColorCMD(this);
        new PartsFilterTagsCMD(this);
        new PartsFilterTypesCMD(this);
        new PartsFilterBlocksCMD(this);
        new PartsFilterItemsCMD(this);
        new PartsRefreshFilterCMD(this);
        new PartsResetFilterCMD(this);
        new PartsAdaptTagsCMD(this);
        new PartsAddTagCMD(this);
        new PartsRemoveTagCMD(this);
        new PartsListTagsCMD(this);
        new PartsRemoveCMD(this);
        new PartsTranslateCMD(this);
        new PartsSetBlockCMD(this);
        new PartsBillboardCMD(this);
        new PartsViewRangeCMD(this);
        new PartsBrightnessCMD(this);
        new PartsPitchCMD(this);
        new PartsYawCMD(this);
        new PartsMoveHereCMD(this);
        new PartsMoveCMD(this);
        new PartsScaleCMD(this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            partsHelp(sender, 1);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            partsHelp(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void partsHelp(CommandSender sender, int page){
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        if (page <= 1){
            sender.sendMessage(Component.text("\"Parts\" are each individual display/interaction entity that is contained within a group", NamedTextColor.AQUA));
            sender.sendMessage(Component.text("| Add tags to parts to identify each part in a group", NamedTextColor.AQUA));
            sender.sendMessage(Component.text("| \"-all\" will apply the command to all parts within your part selection where valid. By default a selected group's parts is your part selection", NamedTextColor.GOLD));
            sender.sendMessage(Component.empty());
            CMDUtils.sendCMD(sender, "/deu parts help <page-number>", "Get help for parts");
            CMDUtils.sendCMD(sender, "/deu parts info", "Get information about your current part/selection");
            CMDUtils.sendCMD(sender, "/deu parts create <block | item | text | interaction>", "Spawn an entity at your location and automatically select it");
            CMDUtils.sendCMD(sender, "/deu parts select <distance | -target>", "Select a nearby, ungrouped, Display entity. Use \"-target\" to select your targeted Interaction");
        }
        else if (page == 2) {
            CMDUtils.sendCMD(sender, "/deu parts cycle <first | prev | next | last> [jump]", "Cycle through parts of your selected group");
            CMDUtils.sendCMD(sender, "/deu parts addtag <part-tag> [-all]", "Add a tag to your selected part");
            CMDUtils.sendCMD(sender, "/deu parts removetag <part-tag> [-all]", "Remove a tag from your selected part");
            CMDUtils.sendCMD(sender, "/deu parts adapttags [-remove]",
                    "Adapt scoreboard tags to tags usable by DisplayEntityUtils. Applied to selected parts."+
                            " \"-remove\" removes tag from scoreboard");
            CMDUtils.sendCMD(sender, "/deu parts listtags ", "List tags of the currently selected part");
            CMDUtils.sendCMD(sender, "/deu parts filtertags <part-tags>", "Filter parts by part tags, comma separated. Exclude A tag by prefixing it with \"!\"");
            CMDUtils.sendCMD(sender, "/deu parts filtertypes <part-types>", "Filter parts by their type. Exclude ALL filtered types by prefixing with \"!\"");
        }
        else if (page == 3){
            CMDUtils.sendCMD(sender, "/deu parts filterblocks <block-ids>", "Filter blocks of BLOCK parts. Exclude ALL filtered blocks by prefixing with \"!\"");
            CMDUtils.sendCMD(sender, "/deu parts filteritems <item-ids>", "Filter items of ITEM parts. Exclude ALL filtered items by prefixing with \"!\"");
            CMDUtils.sendCMD(sender, "/deu parts refreshfilter", "Refresh your part selection after making some type of change");
            CMDUtils.sendCMD(sender, "/deu parts resetfilter", "Reset your part selection and any filters");
            CMDUtils.sendCMD(sender, "/deu parts remove [-all]", "Despawn and remove your selected part from a group");
            CMDUtils.sendCMD(sender, "/deu parts glow [-all]", "Make your selected part glow");
            CMDUtils.sendCMD(sender, "/deu parts unglow [-all]", "Remove the glow from your selected part");
        }
        else if (page == 4){
            CMDUtils.sendCMD(sender, "/deu parts glowcolor <color | hex-code> [-all]", "Set your selected part's glow color");
            CMDUtils.sendCMD(sender, "/deu parts brightness <block> <sky> [-all]", "Set your selected part's brightness. Enter values between 0-15. -1 resets");
            CMDUtils.sendCMD(sender, "/deu parts viewrange <view-range-multiplier> [-all]", "Set the view range multiplier for your selected part");
            CMDUtils.sendCMD(sender, "/deu parts billboard <fixed | vertical | horizontal | center> [-all]", "Set the billboard of your selected part");
            CMDUtils.sendCMD(sender, "/deu parts translate <direction> <distance> <tick-duration> [-all]", "Translate your selected part");
            CMDUtils.sendCMD(sender, "/deu parts pitch <pitch>", "Set the pitch of an ungrouped part entity");
            CMDUtils.sendCMD(sender, "/deu parts yaw <yaw>", "Set the yaw of an ungrouped part entity");
        }
        else{
            CMDUtils.sendCMD(sender, "/deu parts scale <x | y | z | -all> <scale> [-all]", "Change the scale of your selected part, by axis");
            CMDUtils.sendCMD(sender, "/deu parts move <direction> <distance> [-all]", "Change the actual location of your selected part");
            CMDUtils.sendCMD(sender, "/deu parts movehere [-all]", "Change your selected part's actual location to your location");
            CMDUtils.sendCMD(sender, "/deu parts setblock <\"-held\" | \"-target\" | block-id> [-all]", "Change the block of a block display part");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    public static void invalidPartSelection(CommandSender sender){
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your part selection is invalid!", NamedTextColor.RED)));
    }

    public static boolean isUnwantedMultiSelection(Player player, ActivePartSelection<?> selection){
        if (selection instanceof MultiPartSelection){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this with a grouped part!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }

    public static boolean isUnwantedSingleSelection(Player player, ActivePartSelection<?> selection){
        if (selection.isSinglePartSelection()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this with an ungrouped selected part entity!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }

    public static boolean isUnwantedSingleSelectionAll(Player player, ActivePartSelection<?> selection){
        if (selection.isSinglePartSelection()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot use \"-all\" with an ungrouped selected part entity!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }
}
