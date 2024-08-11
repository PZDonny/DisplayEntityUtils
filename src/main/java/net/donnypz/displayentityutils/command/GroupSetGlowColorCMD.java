package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

class GroupSetGlowColorCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_GLOW_COLOR_SET)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + "Enter a valid color!");
            player.sendMessage(Component.text("/mdis group setglowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }

        Color c = getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+"Enter a valid color!");
            player.sendMessage(Component.text("/mdis group setglowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }
        group.setGlowColor(c);
        group.glow(60, true);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Glow color successfully set for display entity group!");
    }

    static Color getColorFromText(String color){
        Color c = null;

        //Vanilla Colors
        if (color.equalsIgnoreCase("white")){
            c = Color.WHITE;
        }
        else if (color.equalsIgnoreCase("silver")){
            c = Color.SILVER;
        }
        else if (color.equalsIgnoreCase("gray")){
            c = Color.GRAY;
        }
        else if (color.equalsIgnoreCase("black")){
            c = Color.BLACK;
        }
        else if (color.equalsIgnoreCase("red")){
            c = Color.RED;
        }
        else if (color.equalsIgnoreCase("maroon")){
            c = Color.MAROON;
        }
        else if (color.equalsIgnoreCase("yellow")){
            c = Color.YELLOW;
        }
        else if (color.equalsIgnoreCase("olive")){
            c = Color.OLIVE;
        }
        else if (color.equalsIgnoreCase("lime")){
            c = Color.LIME;
        }
        else if (color.equalsIgnoreCase("green")){
            c = Color.GREEN;
        }
        else if (color.equalsIgnoreCase("aqua")){
            c = Color.AQUA;
        }
        else if (color.equalsIgnoreCase("teal")){
            c = Color.TEAL;
        }
        else if (color.equalsIgnoreCase("blue")){
            c = Color.BLUE;
        }
        else if (color.equalsIgnoreCase("navy")){
            c = Color.NAVY;
        }
        else if (color.equalsIgnoreCase("fuchsia")){
            c = Color.FUCHSIA;
        }
        else if (color.equalsIgnoreCase("purple")){
            c = Color.PURPLE;
        }
        else if (color.equalsIgnoreCase("orange")){
            c = Color.ORANGE;
        }
        //Hex
        else{
            try{
                String formattedColor = color.replace("0x", "").replace("#", "");
                c = Color.fromRGB(Integer.parseInt(formattedColor, 16));
            }
            catch(IllegalArgumentException ignored){}
        }
        return c;
    }

}
