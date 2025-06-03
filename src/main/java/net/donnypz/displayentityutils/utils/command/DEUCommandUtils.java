package net.donnypz.displayentityutils.utils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class DEUCommandUtils {

    private static final HashMap<UUID, Set<RelativePointDisplay>> relativePointDisplays = new HashMap<>();
    private static final HashMap<UUID, RelativePointDisplay> selectedRelativePoint = new HashMap<>();

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
            FramePointDisplay pd = new FramePointDisplay(spawnLoc, point, frame);
            displays.add(pd);
            pd.reveal(player);
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
            Material material = Material.matchMaterial(block.toLowerCase());
            if (material == null || !material.isBlock()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Block not recognized! The block's name might have been misspelled or the block doesn't exist.", NamedTextColor.RED)));
                return null;
            }
            blockData = material.createBlockData();
        }
        return blockData;
    }

    public static ItemStack getItemFromText(String item, Player player){
        ItemStack itemStack;
        if (item.equals("-held")){
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            itemStack = mainHand.clone();
        }


        //Item-ID
        else{
            Material material = Material.matchMaterial(item.toLowerCase());
            if (material == null){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Item not recognized! The item's name might have been misspelled.", NamedTextColor.RED)));
                return null;
            }
            itemStack = new ItemStack(material);
        }
        return itemStack;
    }

    public static Color getColorFromText(String color){
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

    @ApiStatus.Internal
    public static String getCoordinateString(Location location){
        return location.x()+" "+location.y()+" "+location.z();
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
