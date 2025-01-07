package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class InteractionCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    InteractionCMD(){
        subCommands.put("help", new InteractionHelpCMD());
        subCommands.put("addcmd", new InteractionAddCMD());
        subCommands.put("listcmd", new InteractionListCMD());
        subCommands.put("height", new InteractionHeightCMD());
        subCommands.put("width", new InteractionWidthCMD());
        subCommands.put("scale", new InteractionScaleCMD());
        subCommands.put("pivot", new InteractionPivotCMD());
        subCommands.put("pivotselection", new InteractionPivotSelectionCMD());
        subCommands.put("spawn", new InteractionSpawnCMD());
        subCommands.put("spawnhere", new InteractionSpawnHereCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2){
            interactionHelp(player, 1);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
                return;
            }
            interactionHelp(player, 1);
        }
        else{
            subCommand.execute(player, args);
        }
    }

    static void interactionHelp(CommandSender sender, int page){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        if (page == 1){
            sender.sendMessage(Component.text("Where applicable, these commands prioritize the interaction entity you're looking at over the one you may have selected", NamedTextColor.GRAY));
            CMDUtils.sendCMD(sender, "/mdis interaction help", " (Get help for interactions)");
            CMDUtils.sendCMD(sender, "/mdis interaction spawn <height> <width> (Create an interaction entity part for a group, at the group's location)");
            CMDUtils.sendCMD(sender, "/mdis interaction spawnhere <height> <width> (Create an interaction entity part for a group, at your location)");
            CMDUtils.sendCMD(sender, "/mdis interaction height <height>", " (Set the height of an interaction)");
            CMDUtils.sendCMD(sender, "/mdis interaction width <width>", " (Set the width of an interaction)");
            CMDUtils.sendCMD(sender, "/mdis interaction scale <height> <width> <tick-duration> <tick-delay>", " (Scale an interaction entity over a period of time)");
        }
        else{
            CMDUtils.sendCMD(sender, "/mdis interaction addcmd <player | console> <left | right | both> <command>", " (Add a command to an interaction)");
            CMDUtils.sendCMD(sender, "/mdis interaction listcmds", "(List all commands stored on an interaction)");
            CMDUtils.sendCMD(sender, "/mdis interaction pivot <angle>", " (Pivot an interaction around it's group's actual location center)");
            CMDUtils.sendCMD(sender, "/mdis interaction pivotselection <angle>", " (Pivot all Interactions in a part selection)");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static Interaction getInteraction(Player p, boolean checkTargeted){
        Entity entity = p.getTargetEntity(5);
        if (entity instanceof Interaction i && checkTargeted){
            return i;
        }
        else{
            SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
            if (partSelection == null){
                noPartSelectionInteraction(p);
                return null;
            }
            else{
                if (partSelection.getSelectedParts().isEmpty()){
                    PartsCMD.invalidPartSelection(p);
                    return null;
                }

                SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
                if (selected.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION) {
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with interaction entities", NamedTextColor.RED)));
                    return null;
                }
                return (Interaction) selected.getEntity();
            }
        }
    }

    private static void noPartSelectionInteraction(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You must look at the interaction you wish to add the command to, or select a part!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next | last>", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/mdis parts select <part-tag>", NamedTextColor.GRAY));
    }

}
