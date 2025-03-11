package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class GroupInfoCMD extends PlayerSubCommand {
    GroupInfoCMD() {
        super(Permission.GROUP_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        String groupTag = group.getTag();
        groupTag = groupTag == null ? "<red>NOT SET" : "<yellow>"+groupTag;

        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: <yellow>"+groupTag));
        player.sendMessage(MiniMessage.miniMessage().deserialize("World: <yellow>"+group.getWorldName()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Total Parts: <yellow>"+(group.getSpawnedParts().size())));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Is Persistent: <yellow>"+group.isPersistent()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Chunk Load Persistence Overriding: <yellow>"+group.allowsPersistenceOverriding()));

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
        player.sendMessage(Component.empty());
        Color color = group.getGlowColor();
        if (color != null) {
            player.sendMessage(Component.text("Glow Color: ").append(Component.text("COLOR", TextColor.color(color.getRed(), color.getGreen(), color.getBlue()))));
            player.sendMessage("| " + ChatColor.RED + "R: " + color.getRed());
            player.sendMessage("| " + ChatColor.GREEN + "G: " + color.getGreen());
            player.sendMessage("| " + ChatColor.BLUE + "B: " + color.getBlue());

            String redString = Integer.toHexString(color.getRed());
            if (redString.equals("0")) {
                redString += "0";
            }
            String greenString = Integer.toHexString(color.getGreen());
            if (greenString.equals("0")) {
                greenString += "0";
            }
            String blueString = Integer.toHexString(color.getBlue());
            if (blueString.equals("0")) {
                blueString += "0";
            }
            player.sendMessage("| " + ChatColor.YELLOW + "HEX: #" + redString + greenString + blueString);
        }
        else {
            player.sendMessage("Glow Color: " + ChatColor.RED + "NOT SET");
        }
    }
}
