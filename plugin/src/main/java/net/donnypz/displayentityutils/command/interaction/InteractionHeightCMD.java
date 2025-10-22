package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionHeightCMD extends PlayerSubCommand {
    InteractionHeightCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("height", parentSubCommand, Permission.INTERACTION_DIMENSION);
        setTabComplete(2, "<height>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis interaction height <height>", NamedTextColor.RED));
            return;
        }
        setInteractionDimensions(player, args, "height");
    }

    static void setInteractionDimensions(Player p , String[] args, String dim){
        InteractionCMD.SelectedInteraction i = InteractionCMD.getInteraction(p, true);
        if (i == null){
            return;
        }
        try{
            float change = Float.parseFloat(args[2]);
            if (dim.equals("height")){
                i.setHeight(change);
            }
            else{
                i.setWidth(change);
            }

            p.sendMessage(Component.text("Successfully set interaction's "+dim+" to "+change, NamedTextColor.GREEN));
        }
        catch(NumberFormatException e) {
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid " + dim + ", enter a number!", NamedTextColor.RED)));
        }
    }
}
