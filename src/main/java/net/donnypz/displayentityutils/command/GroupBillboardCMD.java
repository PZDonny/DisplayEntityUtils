package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

class GroupBillboardCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_BILLBOARD)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group billboard <fixed | vertical | horizontal | center>", NamedTextColor.GRAY));
            return;
        }

        try{
            Display.Billboard billboard = Display.Billboard.valueOf(args[2].toUpperCase());
            group.setBillboard(billboard);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Billboard successfully set for your selected group!", NamedTextColor.GREEN)));
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group billboard <fixed | vertical | horizontal | center>\"", NamedTextColor.GRAY));
        }
    }

}
