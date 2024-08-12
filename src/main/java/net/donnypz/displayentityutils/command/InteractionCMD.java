package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        subCommands.put("removecmd", new InteractionRemoveCMD());
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
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis interaction setheight <height>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis interaction setwidth <width>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis interaction addcmd <player | console> <left | right | both> <command>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis interaction removecmd <command-id>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis interaction listcmds");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis interaction pivot <angle>", " (Pivot the currently selected (cycled) Interaction in a part selection)");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis interaction pivotselection <angle>", " (Pivot all Interactions in a part selection)");
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
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next>"));
        player.sendMessage(Component.text("/mdis parts select <part-tag>"));
    }

}
