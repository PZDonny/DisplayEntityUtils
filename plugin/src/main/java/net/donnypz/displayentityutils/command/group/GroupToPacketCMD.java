package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupToPacketCMD extends PlayerSubCommand {
    GroupToPacketCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("topacket", parentSubCommand, Permission.GROUP_TO_PACKET);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length >= 3 && args[2].equals("-confirm")){
            group.toPacket(group.getLocation(), true, true, true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected group is now packet-based!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("The group is stored in its chunk's data. Make sure to save this world", NamedTextColor.YELLOW));
            if (args.length < 4 || !args[3].equals("-keep")){
                DEUUser.getOrCreateUser(player).deselectSpawnedGroup();
                group.unregister(true, true);
            }
            else{
                player.sendMessage(Component.text("| Your selected group was not despawned", NamedTextColor.GRAY, TextDecoration.ITALIC));
            }
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("! Doing this will result in your selected group becoming completely packet based!"
                +" It will become only selectable through the API/Skript", NamedTextColor.RED)));
        player.sendMessage(Component.text("| Execute \"/mdis group topacket -confirm [-keep]\" to confirm this action", NamedTextColor.YELLOW));
    }
}
