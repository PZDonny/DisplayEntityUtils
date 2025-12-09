package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class InteractionAddToGroupCMD extends GroupSubCommand {
    InteractionAddToGroupCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addtogroup", parentSubCommand, Permission.INTERACTION_SPAWN, 4, true);
        setTabComplete(2, "<height>");
        setTabComplete(3, "<width>");
        setTabComplete(4, "-here");
    }


    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu interaction addtogroup <height> <width> [-here]", NamedTextColor.RED)));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        Location spawn;
        if (args.length >= 5){
            spawn = args[4].equalsIgnoreCase("-here") ? player.getLocation() : group.getLocation();
        }
        else{
            spawn = group.getLocation();
        }
        if (group instanceof SpawnedDisplayEntityGroup sg){
            Interaction i = spawnInteraction(player, spawn, args);
            if (i == null) return;
            sg.addInteractionEntity(i);
        }
        else if (group instanceof PacketDisplayEntityGroup pg){
            spawnInteraction(pg, player, spawn, args);
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("| The interaction has been added to your group!", NamedTextColor.YELLOW)));
        MultiPartSelection<?> sel = (MultiPartSelection<?>) DisplayGroupManager.getPartSelection(player);
        sel.refresh();
    }

    static Interaction spawnInteraction(Player player, Location spawnLoc, String[] args){
        try{

            float height = Float.parseFloat(args[2]);
            float width = Float.parseFloat(args[3]);

            Interaction interaction = spawnLoc.getWorld().spawn(spawnLoc, Interaction.class, i -> {
                i.setInteractionHeight(height);
                i.setInteractionWidth(width);
            });
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Spawned a new Interaction entity", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Height: "+height, NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Width: "+width, NamedTextColor.GRAY));
            return interaction;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            return null;
        }
    }

    static void spawnInteraction(PacketDisplayEntityGroup group, Player player, Location spawnLoc, String[] args){
        try{
            float height = Float.parseFloat(args[2]);
            float width = Float.parseFloat(args[3]);

            PacketDisplayEntityPart part = new PacketAttributeContainer()
                    .setAttribute(DisplayAttributes.Interaction.HEIGHT, height)
                    .setAttribute(DisplayAttributes.Interaction.WIDTH, width)
                    .createPart(SpawnedDisplayEntityPart.PartType.INTERACTION, spawnLoc);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Spawned a new packet-based Interaction entity", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Height: "+height, NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Width: "+width, NamedTextColor.GRAY));
            group.addPart(part);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
        }
    }
}