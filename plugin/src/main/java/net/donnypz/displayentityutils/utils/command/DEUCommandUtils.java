package net.donnypz.displayentityutils.utils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ApiStatus.Internal
public class DEUCommandUtils {

    public static void removeRelativePoint(Player player, RelativePointSelector<?> selector){
        UUID playerUUID = player.getUniqueId();
        RelativePointUtils.selectedSelector.remove(playerUUID, selector);
        Set<RelativePointSelector<?>> displays = RelativePointUtils.relativePointSelectors.get(playerUUID);
        displays.remove(selector);
        if (displays.isEmpty()){
            RelativePointUtils.relativePointSelectors.remove(playerUUID);
        }
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
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must be holding a block to do that!", NamedTextColor.RED)));
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
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Block not found, target a block within "+targetDistance+" of you", NamedTextColor.RED)));
                return null;
            }
            b = result.getHitBlock();
            blockData = b.getBlockData();
        }

        //Block-ID
        else{
            Material blockType = Registry.MATERIAL.get(NamespacedKey.minecraft(block.toLowerCase().replace(".", "_")));
            if (blockType == null || !blockType.isBlock()){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Block not recognized! The block's name might have been misspelled or the block doesn't exist.", NamedTextColor.RED)));
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
            Material itemType = Registry.MATERIAL.get(NamespacedKey.minecraft(item.toLowerCase().replace(".", "_")));
            if (itemType == null || !itemType.isItem()){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Item not recognized! The item's name might have been misspelled.", NamedTextColor.RED)));
                return null;
            }
            return new ItemStack(itemType);
        }
    }

    public static void sendGlowColor(Player player, Color color){
        if (color != null) {
            player.sendMessage(Component.text("Glow Color: ").append(Component.text("COLOR", TextColor.color(color.getRed(), color.getGreen(), color.getBlue()))));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| <red>"+color.getRed()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| <green>"+color.getGreen()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| <blue>"+color.getBlue()));

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
}
