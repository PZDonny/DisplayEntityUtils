package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsBillboardCMD extends PartsSubCommand {
    PartsBillboardCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("billboard", parentSubCommand, Permission.PARTS_BILLBOARD, 3, 3);
        setTabComplete(2, TabSuggestion.BILLBOARDS);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu parts billboard <fixed | vertical | horizontal | center> [-all]", NamedTextColor.GRAY));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Display.Billboard billboard = getBillboard(player, args[2]);
        if (billboard == null) return false;
        selection.setBillboard(billboard);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Billboard successfully set for selected display entity part(s) in your selection!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (!selectedPart.isDisplay()) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Only display entities can have a billboard applied!", NamedTextColor.RED)));
        }
        else{
            Display.Billboard billboard = getBillboard(player, args[2]);
            if (billboard == null) return false;
            selectedPart.setBillboard(billboard);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Billboard successfully set for your selected part!", NamedTextColor.GREEN)));
        }
        return true;
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
