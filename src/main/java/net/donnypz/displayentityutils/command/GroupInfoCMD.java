package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class GroupInfoCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_INFO)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        String groupTag = group.getTag() == null ? ChatColor.RED + "NOT SET" : ChatColor.YELLOW + group.getTag();

        player.sendMessage("Group Tag: " + ChatColor.YELLOW + groupTag);
        player.sendMessage("Total Parts: " + (group.getSpawnedParts().size() - 1));
        player.sendMessage("Pitch: " + group.getLocation().getPitch());
        player.sendMessage("Yaw: " + group.getLocation().getYaw());
        player.sendMessage("Group's Scale Multiplier: " + ChatColor.YELLOW + group.getScaleMultiplier() + "x");
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
        } else {
            player.sendMessage("Glow Color: " + ChatColor.RED + "NOT SET");
        }
    }
}
