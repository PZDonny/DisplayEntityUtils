package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GroupInfoCMD extends PlayerSubCommand {
    GroupInfoCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("info", parentSubCommand, Permission.GROUP_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {

        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefixLong);
        String groupTag = group.getTag();
        groupTag = groupTag == null ? "<red>NOT SET" : "<yellow>"+groupTag;

        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: <yellow>"+groupTag));
        player.sendMessage(MiniMessage.miniMessage().deserialize("World: <yellow>"+group.getWorldName()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Total Parts: <yellow>"+(group.getParts().size())));
        if (group instanceof SpawnedDisplayEntityGroup sg){
            player.sendMessage(MiniMessage.miniMessage().deserialize("Is Packet Based: <red>FALSE"));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Is Persistent: <yellow>"+sg.isPersistent()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Chunk Load Persistence Overriding: <yellow>"+sg.allowsPersistenceOverriding()));
        }
        else if (group instanceof PacketDisplayEntityGroup pg){
            player.sendMessage(MiniMessage.miniMessage().deserialize("Is Packet Based: <green>TRUE"));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Is Persistent (Exists after restart): <yellow>"+pg.isPersistent()));
        }

        Location loc = group.getLocation();
        player.sendMessage(MiniMessage.miniMessage().deserialize("Pitch & Yaw: <yellow>"+loc.getPitch()+", "+loc.getYaw()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Scale Multiplier: <yellow>"+group.getScaleMultiplier()));

        String animTag = group.getSpawnAnimationTag();
        animTag = animTag == null ? "<red>NOT SET" : "<yellow>"+animTag;

        DisplayAnimator.AnimationType type = group.getSpawnAnimationType();
        String animType = type == null ? "<red>NOT SET" : "<yellow>"+type.name();

        LoadMethod loadMethod = group.getSpawnAnimationLoadMethod();
        String animLoadMethod = loadMethod == null ? "<red>NOT SET" : "<yellow>"+loadMethod.name();

        player.sendMessage(MiniMessage.miniMessage().deserialize("Spawn Animation Tag: "+animTag));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Spawn Animation Type: "+animType));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Spawn Animation Storage: "+animLoadMethod));
        DEUCommandUtils.sendGlowColor(player, group.getGlowColor());
    }
}
