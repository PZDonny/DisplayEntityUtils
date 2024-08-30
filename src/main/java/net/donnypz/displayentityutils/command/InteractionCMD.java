package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class InteractionCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    InteractionCMD(){
        subCommands.put("addcmd", new InteractionAddCMD());
        subCommands.put("listcmd", new InteractionListCMD());
        subCommands.put("setheight", new InteractionSetHeightCMD());
        subCommands.put("setwidth", new InteractionSetWidthCMD());
        subCommands.put("pivot", new InteractionPivotCMD());
        subCommands.put("pivotselection", new InteractionPivotSelectionCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2){
            interactionHelp(player);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
                return;
            }
            interactionHelp(player);
        }
        else{
            subCommand.execute(player, args);
        }
    }

    static void interactionHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(Component.text("| These commands prioritize the interaction entity you're looking at over the one you may have selected"));
        CMDUtils.sendCMD(sender,"/mdis interaction setheight <height>", " (Set the height of an interaction)");
        CMDUtils.sendCMD(sender,"/mdis interaction setwidth <width>", " (Set the width of an interaction)");
        CMDUtils.sendCMD(sender,"/mdis interaction addcmd <player | console> <left | right | both> <command>", " (Add a command to an interaction)");
        CMDUtils.sendCMD(sender,"/mdis interaction listcmds", "(List all commands stored on an interaction)");
        CMDUtils.sendCMD(sender,"/mdis interaction pivot <angle>", " (Pivot an interaction around it's group's actual location center)");
        CMDUtils.sendCMD(sender,"/mdis interaction pivotselection <angle>", " (Pivot all Interactions in a part selection)");
    }

    static Interaction getInteraction(Player p, boolean allowTargeted){
        Entity entity = p.getTargetEntity(5);
        if (entity instanceof Interaction i && allowTargeted){
            return i;
        }
        else{
            SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
            if (partSelection == null){
                noPartSelectionInteraction(p);
                return null;
            }
            else{
                if (partSelection.getSelectedParts().size() > 1){
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with one part selected");
                    return null;
                }
                if (partSelection.getSelectedParts().getFirst().getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with interaction entities");
                    return null;
                }
                return (Interaction) partSelection.getSelectedParts().getFirst().getEntity();
            }
        }
    }

    private static void noPartSelectionInteraction(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You must look at the interaction you wish to add the command to, or select a part!");
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next>", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/mdis parts select <part-tag>", NamedTextColor.GRAY));
    }

}
