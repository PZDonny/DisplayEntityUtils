package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public final class InteractionCMD extends ConsoleUsableSubCommand {

    public InteractionCMD(){
        super(Permission.HELP, new InteractionHelpCMD());
        new InteractionAddCMD(this);
        new InteractionListCMD(this);
        new InteractionHeightCMD(this);
        new InteractionWidthCMD(this);
        new InteractionScaleCMD(this);
        new InteractionPivotCMD(this);
        new InteractionPivotSelectionCMD(this);
        new InteractionResponsiveCMD(this);
        new InteractionSpawnCMD(this);
        new InteractionSpawnHereCMD(this);
        new InteractionInfoCMD(this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            interactionHelp(sender, 1);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            interactionHelp(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void interactionHelp(CommandSender sender, int page){
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        if (page == 1){
            sender.sendMessage(Component.text("Where applicable, these commands prioritize the interaction entity you're looking at over the one you may have selected", NamedTextColor.AQUA));
            CMDUtils.sendCMD(sender, "/mdis interaction help", "Get help for interactions");
            CMDUtils.sendCMD(sender, "/mdis interaction info", "Get info about an interaction entity, targeted or selected");
            CMDUtils.sendCMD(sender, "/mdis interaction spawn <height> <width>", "Create an interaction entity part for a group, at the group's location");
            CMDUtils.sendCMD(sender, "/mdis interaction spawnhere <height> <width>", "Create an interaction entity at your location");
            CMDUtils.sendCMD(sender, "/mdis interaction height <height>", "Set the height of an interaction");
            CMDUtils.sendCMD(sender, "/mdis interaction width <width>", "Set the width of an interaction");
        }
        else{
            CMDUtils.sendCMD(sender, "/mdis interaction scale <height> <width> <tick-duration> <tick-delay>", "Scale an interaction entity over a period of time");
            CMDUtils.sendCMD(sender, "/mdis interaction addcmd <player | console> <left | right | both> <command>", "Add a command to an interaction");
            CMDUtils.sendCMD(sender, "/mdis interaction listcmds", "List all commands stored on an interaction");
            CMDUtils.sendCMD(sender, "/mdis interaction pivot <angle>", " Pivot an interaction around it's group's actual location center");
            CMDUtils.sendCMD(sender, "/mdis interaction pivotselection <angle>", "Pivot all Interactions included in your selected group's part filter");
            CMDUtils.sendCMD(sender, "/mdis interaction responsive", "Toggle the hit sound of an interaction entity");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static Interaction getInteraction(Player player, boolean checkTargeted){
        Entity entity = player.getTargetEntity(5);
        if (entity instanceof Interaction i && checkTargeted){
            return i;
        }
        else{
            ServerSideSelection selection = DisplayGroupManager.getPartSelection(player);
            SpawnedPartSelection partSelection = (SpawnedPartSelection) selection;
            if (partSelection == null){
                noPartSelectionInteraction(player);
                return null;
            }
            else{
                if (partSelection.getSelectedParts().isEmpty()){
                    PartsCMD.invalidPartSelection(player);
                    return null;
                }

                SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
                if (selected.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION) {
                    player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with interaction entities", NamedTextColor.RED)));
                    return null;
                }
                return (Interaction) selected.getEntity();
            }
        }
    }

    private static void noPartSelectionInteraction(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must look at the interaction you wish to add the command to, or select a part!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next | last>", NamedTextColor.GRAY));
    }

}
