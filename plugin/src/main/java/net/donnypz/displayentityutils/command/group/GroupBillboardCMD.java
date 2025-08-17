package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupBillboardCMD extends PlayerSubCommand {
    GroupBillboardCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("billboard", parentSubCommand, Permission.GROUP_BILLBOARD);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group billboard <fixed | vertical | horizontal | center>", NamedTextColor.GRAY));
            return;
        }

        try{
            Display.Billboard billboard = Display.Billboard.valueOf(args[2].toUpperCase());
            group.setBillboard(billboard);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Billboard successfully set for your selected group!", NamedTextColor.GREEN)));
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group billboard <fixed | vertical | horizontal | center>\"", NamedTextColor.GRAY));
        }
    }

}
