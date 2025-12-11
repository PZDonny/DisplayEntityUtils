package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupBillboardCMD extends GroupSubCommand {
    GroupBillboardCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("billboard", parentSubCommand, Permission.GROUP_BILLBOARD, 3, true);
        setTabComplete(2, TabSuggestion.BILLBOARDS);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu group billboard <fixed | vertical | horizontal | center>", NamedTextColor.GRAY));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        try{
            Display.Billboard billboard = Display.Billboard.valueOf(args[2].toUpperCase());
            group.setBillboard(billboard);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Billboard successfully set for your selected group!", NamedTextColor.GREEN)));
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/deu group billboard <fixed | vertical | horizontal | center>\"", NamedTextColor.GRAY));
        }
    }

}
