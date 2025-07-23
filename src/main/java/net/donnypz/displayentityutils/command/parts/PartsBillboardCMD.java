package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsBillboardCMD extends PartsSubCommand {
    PartsBillboardCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("billboard", parentSubCommand, Permission.PARTS_BILLBOARD, 3, 3);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis parts billboard <fixed | vertical | horizontal | center> [-all]", NamedTextColor.GRAY));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        Display.Billboard billboard = getBillboard(player, args[2]);
        if (billboard == null) return;
        selection.setBillboard(billboard);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Billboard successfully set for selected display entity part(s) in your selection!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        if (selectedPart.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Interaction entities cannot have a billboard applied!", NamedTextColor.RED)));
        }
        else{
            Display.Billboard billboard = getBillboard(player, args[2]);
            if (billboard == null) return;
            selectedPart.setBillboard(billboard);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Billboard successfully set for your selected part!", NamedTextColor.GREEN)));
        }
    }

    private Display.Billboard getBillboard(Player player, String arg){
        try{
            return Display.Billboard.valueOf(arg.toUpperCase());
        }
        catch(IllegalArgumentException e){
            sendIncorrectUsage(player);
            return null;
        }
    }
}
