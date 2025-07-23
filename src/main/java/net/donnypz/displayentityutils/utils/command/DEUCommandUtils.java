package net.donnypz.displayentityutils.utils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class DEUCommandUtils {

    private static final HashMap<UUID, Set<RelativePointDisplay>> relativePointDisplays = new HashMap<>();
    private static final HashMap<UUID, RelativePointDisplay> selectedRelativePoint = new HashMap<>();
    private static final Map<String, Color> defaultColors = Map.ofEntries(
            Map.entry("white", Color.WHITE),
            Map.entry("silver", Color.SILVER),
            Map.entry("gray", Color.GRAY),
            Map.entry("black", Color.BLACK),
            Map.entry("red", Color.RED),
            Map.entry("maroon", Color.MAROON),
            Map.entry("yellow", Color.YELLOW),
            Map.entry("olive", Color.OLIVE),
            Map.entry("lime", Color.LIME),
            Map.entry("green", Color.GREEN),
            Map.entry("aqua", Color.AQUA),
            Map.entry("teal", Color.TEAL),
            Map.entry("blue", Color.BLUE),
            Map.entry("navy", Color.NAVY),
            Map.entry("fuchsia", Color.FUCHSIA),
            Map.entry("purple", Color.PURPLE),
            Map.entry("orange", Color.ORANGE));

    @ApiStatus.Internal
    public static void spawnFramePointDisplays(SpawnedDisplayEntityGroup group, Player player, SpawnedDisplayAnimationFrame frame){
        if (isViewingRelativePoints(player)) {
            player.sendMessage(Component.text("You are already viewing points!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Run \"/mdis anim cancelpoints\" to stop viewing points", NamedTextColor.GRAY));
            return;
        }

        if (!frame.hasFramePoints()){
            player.sendMessage(Component.text("Failed to view points! The frame does not have any frame points!", NamedTextColor.RED));
            return;
        }

        Set<RelativePointDisplay> displays = new HashSet<>();
        Set<FramePoint> points = frame.getFramePoints();

        for (FramePoint point : points){
            Location spawnLoc = point.getLocation(group);
            spawnLoc.setPitch(0);
            FramePointDisplay pd = new FramePointDisplay(player, spawnLoc, point, frame);
            displays.add(pd);
        }

        relativePointDisplays.put(player.getUniqueId(), displays);
        player.sendMessage(Component.text("Left click a point to select it", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("| Run \"/mdis anim cancelpoints\" to stop viewing points", NamedTextColor.GRAY));
    }

    //Remove the visual representation of RelativePoints
    public static boolean removeRelativePoints(Player player){
        if (player == null) return false;
        Set<RelativePointDisplay> displays = relativePointDisplays.remove(player.getUniqueId());
        if (displays != null){
            for (RelativePointDisplay d : displays){
                d.despawn();
            }
        }
        deselectRelativePoint(player);
        return displays != null;
    }

    public static void removeRelativePoint(Player player, RelativePointDisplay point){
        UUID playerUUID = player.getUniqueId();
        selectedRelativePoint.remove(playerUUID, point);
        Set<RelativePointDisplay> displays = relativePointDisplays.get(playerUUID);
        displays.remove(point);
        if (displays.isEmpty()){
            relativePointDisplays.remove(playerUUID);
        }
    }


    /**
     * Check if a player has the visual representation of {@link RelativePoint}s, which are visible after command execution from a player
     * @param player
     */
    public static boolean isViewingRelativePoints(Player player){
        return relativePointDisplays.containsKey(player.getUniqueId());
    }


    public static RelativePointDisplay getSelectedRelativePoint(Player player){
        return selectedRelativePoint.get(player.getUniqueId());
    }

    public static void selectRelativePoint(Player player, RelativePointDisplay relativePoint){
        RelativePointDisplay oldPoint = selectedRelativePoint.put(player.getUniqueId(), relativePoint);
        if (oldPoint != null){
            oldPoint.deselect();
        }
        relativePoint.select();
        player.sendMessage(Component.text("Clicked Point Selected!", NamedTextColor.GREEN));
    }

    public static void deselectRelativePoint(Player player){
        selectedRelativePoint.remove(player.getUniqueId());
    }

    public static int[] commaSeparatedIDs(String idString) throws IllegalArgumentException{
        String[] split = idString.split(",");
        if (split.length == 0){
            return new int[]{Integer.parseInt(idString)};
        }

        int[] arr = new int[split.length];
        for (int i = 0; i < split.length; i++){
            arr[i] = Integer.parseInt(split[i]);
        }
        return arr;
    }

    public static Collection<SpawnedDisplayAnimationFrame> getFrames(String arg, SpawnedDisplayAnimation animation) throws IllegalArgumentException{
        //Single Frame ID in arg
        try{
            int index = Integer.parseInt(arg);
            return Set.of(animation.getFrame(index));
        }


        catch(NumberFormatException ignored){}
        catch(IndexOutOfBoundsException e){ //Single Frame ID Out of Bounds
            throw new IllegalArgumentException(e);
        }

        //Multiple Frame IDs
        try{
            int[] ids = commaSeparatedIDs(arg);
            Set<SpawnedDisplayAnimationFrame> frames = new HashSet<>();
            for (int i = 0; i < ids.length; i++){
                try{
                    frames.add(animation.getFrame(i));
                }
                catch(IndexOutOfBoundsException ignored1){}
            }
            return frames;
        }
        //Single Frame Tag
        catch (IllegalArgumentException ex){
            if (!DisplayUtils.isValidTag(arg)){
                throw ex;
            }

            Set<SpawnedDisplayAnimationFrame> frames = new HashSet<>();
            for (SpawnedDisplayAnimationFrame frame : animation.getFrames()){
                if (arg.equals(frame.getTag())){
                    frames.add(frame);
                }
            }
            return frames;
        }
    }




    public static BlockData getBlockFromText(String block, Player player){
        BlockData blockData;
        if (block.equals("-held")){
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (!mainHand.getType().isBlock()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You must be holding a block to do that!", NamedTextColor.RED)));
                return null;
            }
            blockData = mainHand.getType().createBlockData();
        }

        //Target Block
        else if (block.equals("-target")){
            int targetDistance = 30;
            RayTraceResult result = player.rayTraceBlocks(targetDistance);
            Block b = null;
            if (result != null){
                b = result.getHitBlock();
            }
            if (result == null || b == null){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Block not found, target a block within "+targetDistance+" of you", NamedTextColor.RED)));
                return null;
            }
            b = result.getHitBlock();
            blockData = b.getBlockData();
        }

        //Block-ID
        else{
            BlockType blockType = Registry.BLOCK.get(NamespacedKey.minecraft(block.toLowerCase().replace(".", "_")));
            if (blockType == null){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Block not recognized! The block's name might have been misspelled or the block doesn't exist.", NamedTextColor.RED)));
                return null;
            }
            blockData = blockType.createBlockData();
        }
        return blockData;
    }

    public static ItemStack getItemFromText(String item, Player player){
        if (item.equals("-held")){
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            return mainHand.clone();
        }

        //Item-ID
        else{
            ItemType itemType = Registry.ITEM.get(NamespacedKey.minecraft(item.toLowerCase().replace(".", "_")));
            if (itemType == null){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Item not recognized! The item's name might have been misspelled.", NamedTextColor.RED)));
                return null;
            }
            return itemType.createItemStack();
        }
    }

    public static Color getColorFromText(String color){
        Color c = defaultColors.get(color.toLowerCase());

        if (c == null){ //Hex
            try{
                if (color.startsWith("0x")){
                    color = color.substring(2);
                }
                else if (color.startsWith("#")){
                    color = color.substring(1);
                }
                if (color.length() == 8){
                    String col = color.substring(0, 6);
                    String alpha = color.substring(6, 8);
                    c = Color.fromRGB(Integer.parseInt(col, 16))
                            .setAlpha(Integer.parseInt(alpha, 16));
                }
                else if (color.length() == 6){
                    c = Color.fromRGB(Integer.parseInt(color, 16));
                }
            }
            catch(IllegalArgumentException ignored){}
        }
        return c;
    }

    public static void sendGlowColor(Player player, Color color){
        if (color != null) {
            player.sendMessage(Component.text("Glow Color: ").append(Component.text("COLOR", TextColor.color(color.getRed(), color.getGreen(), color.getBlue()))));
            player.sendMessage("| " + net.md_5.bungee.api.ChatColor.RED + "R: " + color.getRed());
            player.sendMessage("| " + net.md_5.bungee.api.ChatColor.GREEN + "G: " + color.getGreen());
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
            String hex = "#"+redString+greenString+blueString;
            player.sendMessage(Component.text("| HEX: "+hex, NamedTextColor.YELLOW)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.copyToClipboard(hex)));
        }
        else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("Glow Color: <red>NOT SET"));
        }
    }

    @ApiStatus.Internal
    public static String getCoordinateString(Location location){
        return round(location.x())+" "+round(location.y())+" "+round(location.z());
    }

    private static double round(double coord){
        return Math.round(coord * 100)/100.0;
    }


    @ApiStatus.Internal
    public static String getExecuteCommandWorldName(World w){
        String worldName;
        if (w.equals(Bukkit.getWorlds().getFirst())){
            worldName = "overworld";
        }
        else{
            worldName = w.getName();
        }
        return worldName;
    }

}
