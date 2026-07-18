package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.PivotAxis;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class InteractionPivotCMD extends PlayerSubCommand {
    InteractionPivotCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pivot", parentSubCommand, Permission.INTERACTION_PIVOT);
        setTabComplete(2, List.of("x", "y", "z"));
        setTabComplete(3, "<angle>");
        addFlag("-all");
        addFlag("-local");
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        PivotAxis axis = CMDUtils.getPivotAxis(args[2], player);
        if (axis == null) return;

        if (!hasMinimumArguments(player, args)) return;

        float angle;
        try{
            angle = Float.parseFloat(args[3]);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the angle!", NamedTextColor.RED)));
            return;
        }


        MultiPartSelection<?> selection = (MultiPartSelection<?>) sel;
        boolean isAll = getOptionalArguments(player, args).hasFlag("-all");
        if (isAll){
            for (ActivePart p : selection.getSelectedParts()){
                if (p.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                    p.pivot(angle, axis);
                }
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivoting ALL Interaction entities in your selection around your group", NamedTextColor.GREEN)));
        }
        else{
            InteractionCMD.SelectedInteraction interaction = InteractionCMD.getInteraction(player, false);
            if (interaction == null){
                return;
            }
            interaction.pivot(selection.getGroup().getLocation(), angle, axis);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivoting Interaction around group", NamedTextColor.GREEN)));
        }
    }

    @Override
    protected String getDescription() {
        return "Pivot an interaction around its group's location. Pivot around the X (pitch), Y (yaw), or Z (roll) axes";
    }
}
