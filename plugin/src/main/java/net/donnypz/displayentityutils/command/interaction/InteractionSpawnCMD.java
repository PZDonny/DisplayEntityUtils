package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.CMDUtils;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionSpawnCMD extends PlayerSubCommand {
    InteractionSpawnCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawn", parentSubCommand, Permission.INTERACTION_SPAWN);
        setTabComplete(2, "<height>");
        setTabComplete(3, "<width>");
        addFlag("-g");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) return;

        Interaction interaction = spawnInteraction(player, player.getLocation(), args);
        CMDUtils.tryAddEntityToGroup(player, args, interaction, this, "-g");
    }

    private Interaction spawnInteraction(Player player, Location spawnLoc, String[] args){
        try{

            float height = Float.parseFloat(args[2]);
            float width = Float.parseFloat(args[3]);

            Interaction interaction = spawnLoc.getWorld().spawn(spawnLoc, Interaction.class, i -> {
                i.setInteractionHeight(height);
                i.setInteractionWidth(width);
            });
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("A new interaction has been spawned at your location!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Height: "+height, NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Width: "+width, NamedTextColor.GRAY));
            return interaction;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            return null;
        }
    }

    @Override
    protected String getDescription() {
        return "Spawn an interaction entity at your location.\nUse \"-g\" to add it to your selected group";
    }
}