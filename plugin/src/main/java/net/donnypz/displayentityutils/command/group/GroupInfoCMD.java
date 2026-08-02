package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

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
        player.sendMessage(MiniMessage.miniMessage().deserialize("Total Parts: <yellow>"+(group.getParts().size())));


        Component persistence;
        if (group.isPersistent()){
            persistence = MiniMessage.miniMessage().deserialize("Is Persistent: <green>TRUE");
            if (group instanceof PacketDisplayEntityGroup pdeg){
                persistence = persistence
                        .append(MiniMessage.miniMessage().deserialize(" <gold>[COPY ID]")
                                .clickEvent(ClickEvent.copyToClipboard(pdeg.getPersistentGlobalId())));
            }
        }
        else{
            persistence = MiniMessage.miniMessage().deserialize("Is Persistent: <red>FALSE");
        }

        String packetBased = group instanceof PacketDisplayEntityGroup ? "<green>TRUE" : "<red>FALSE";
        player.sendMessage(MiniMessage.miniMessage().deserialize("Is Packet Based: "+packetBased));
        player.sendMessage(persistence);

        if (group instanceof SpawnedDisplayEntityGroup sg){
            player.sendMessage(MiniMessage.miniMessage().deserialize("Chunk Load Persistence Overriding: <yellow>"+sg.allowsPersistenceOverriding()));
        }

        Location loc = group.getLocation();
        player.sendMessage(MiniMessage.miniMessage().deserialize("Pitch & Yaw: <yellow>"+loc.getPitch()+", "+loc.getYaw()));
        Vector3f euler = group.getGroupRotation().getEulerAnglesXYZ(new Vector3f());
        float eulerXDeg = (float) Math.toDegrees(euler.x);
        float eulerYDeg = (float) Math.toDegrees(euler.y);
        float eulerZDeg = (float) Math.toDegrees(euler.z);
        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Rotation (X, Y, Z): <yellow>"
                +eulerXDeg+", "
                +eulerYDeg+", "
                +eulerZDeg));
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
        CMDUtils.sendGlowColor(player, group.getGlowColor());
    }

    @Override
    protected String getDescription() {
        return "List information about your selected group";
    }
}
