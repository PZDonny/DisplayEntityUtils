package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.List;

public final class InteractionCMD extends ConsoleUsableSubCommand {

    public InteractionCMD(){
        super(Permission.HELP, new InteractionHelpCMD());
        new InteractionAddCMD(this);
        new InteractionListCMD(this);
        new InteractionHeightCMD(this);
        new InteractionWidthCMD(this);
        new InteractionScaleCMD(this);
        new InteractionPivotCMD(this);
        new InteractionResponsiveCMD(this);
        new InteractionAddToGroupCMD(this);
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
            CMDUtils.sendCMD(sender, "/deu interaction help", "Get help for interactions");
            CMDUtils.sendCMD(sender, "/deu interaction info", "Get info about an interaction entity, targeted or selected");
            CMDUtils.sendCMD(sender, "/deu interaction addtogroup <height> <width> [-here]", "Create an interaction entity part for a group, at the group's location. Use \"-here\" to spawn it at your location");
            CMDUtils.sendCMD(sender, "/deu interaction spawnhere <height> <width>", "Create an interaction entity at your location");
            CMDUtils.sendCMD(sender, "/deu interaction height <height>", "Set the height of an interaction");
            CMDUtils.sendCMD(sender, "/deu interaction width <width>", "Set the width of an interaction");
        }
        else{
            CMDUtils.sendCMD(sender, "/deu interaction scale <height> <width> [tick-duration] [tick-delay]", "Scale an interaction entity, optionally over a period of time");
            CMDUtils.sendCMD(sender, "/deu interaction addcmd <player | console> <left | right | both> <command>", "Add a command to an interaction");
            CMDUtils.sendCMD(sender, "/deu interaction listcmds", "List all commands stored on an interaction");
            CMDUtils.sendCMD(sender, "/deu interaction pivot <angle> [-all]", " Pivot an interaction around it's group's");
            CMDUtils.sendCMD(sender, "/deu interaction responsive", "Toggle the hit sound of an interaction entity");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static SelectedInteraction getInteraction(Player player, boolean checkTargeted){
        Entity entity = player.getTargetEntity(5);
        if (entity instanceof Interaction i && checkTargeted){
            return new SelectedInteraction(i);
        }
        else{
            ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(player);
            if (selection == null){
                return null;
            }
            ActivePart part = selection.getSelectedPart();
            if (part == null || part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with interaction entities", NamedTextColor.RED)));
                player.sendMessage(Component.text("| The Interaction cannot be in a packet-based group", NamedTextColor.GREEN));
                return null;
            }
            return new SelectedInteraction(part);
        }
    }

    static class SelectedInteraction {

        Interaction interaction;
        ActivePart interactionPart;
        SelectedInteraction(Interaction i){
            this.interaction = i;
        }

        SelectedInteraction(ActivePart part){
            this.interactionPart = part;
        }

        void setHeight(float height){
            if (interaction != null){
                interaction.setInteractionHeight(height);
            }
            else{
                interactionPart.setInteractionHeight(height);
                updatePacketGroup();
            }
        }

        void setWidth(float width){
            if (interaction != null){
                interaction.setInteractionWidth(width);
            }
            else{
                interactionPart.setInteractionWidth(width);
                updatePacketGroup();
            }
        }

        void scale(float height, float width, int duration, int delay){
            if (interaction != null){
                DisplayUtils.scaleInteraction(interaction, height, width, duration, delay);
            }
            else{
                if (interactionPart instanceof SpawnedDisplayEntityPart sp){
                    DisplayUtils.scaleInteraction((Interaction) sp.getEntity(), height, width, duration, delay);
                }
                else if (interactionPart instanceof PacketDisplayEntityPart pp){
                    PacketUtils.scaleInteraction(pp, height, width, duration, delay);
                    updatePacketGroup();
                }
            }
        }

        void setResponsive(boolean responsive){
            if (interaction != null){
                interaction.setResponsive(responsive);
            }
            else{
                interactionPart.setInteractionResponsive(responsive);
                updatePacketGroup();
            }
        }

        void pivot(Location pivotLoc, double angle){
            if (interaction != null){
                DisplayUtils.pivot(interaction, pivotLoc, angle);
            }
            else{
                interactionPart.pivot((float) angle);
                updatePacketGroup();
            }
        }

        float getHeight(){
            return interaction == null ? interactionPart.getInteractionHeight() : interaction.getInteractionHeight();
        }

        float getWidth(){
            return interaction == null ? interactionPart.getInteractionWidth() : interaction.getInteractionWidth();
        }

        boolean isResponsive(){
            return interaction == null ? interactionPart.isInteractionResponsive() : interaction.isResponsive();
        }

        String getGroupTag(){
            if (interaction != null){
                return DisplayUtils.getGroupTag(interaction);
            }
            else{
                ActiveGroup<?> group = interactionPart.getGroup();
                return group == null ? null : group.getTag();
            }
        }

        void addInteractionCommand(String command, boolean left, boolean console){
            if (interaction != null){
                DisplayUtils.addInteractionCommand(interaction, command, left, console);
            }
            else{
                interactionPart.addInteractionCommand(command, left, console);
                updatePacketGroup();
            }
        }

        void removeInteractionCommand(InteractionCommand command){
            if (interaction != null){
                DisplayUtils.removeInteractionCommand(interaction, command);
            }
            else{
                interactionPart.removeInteractionCommand(command);
                updatePacketGroup();
            }
        }

        List<String> getInteractionCommands(){
            if (interaction != null){
                return DisplayUtils.getInteractionCommands(interaction);
            }
            else{
                return interactionPart.getInteractionCommands();
            }
        }

        List<InteractionCommand> getInteractionCommandsWithData(){
            if (interaction != null){
                return DisplayUtils.getInteractionCommandsWithData(interaction);
            }
            else{
                return interactionPart.getInteractionCommandsWithData();
            }
        }

        private void updatePacketGroup(){
            if (interactionPart instanceof PacketDisplayEntityPart part){
                part.getGroup().update();
            }
        }
    }
}
