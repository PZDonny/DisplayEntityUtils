package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.Axis;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CMDUtils {

    private static final Component UNSAFE = Component.text("[UNSAFE] ", NamedTextColor.RED);
    private static final TextColor COMMAND_COLOR = TextColor.color(230, 230, 230);

    public static void sendCMD(CommandSender sender, DEUSubCommand deuSubCommand){
        Component msg = Component.text(deuSubCommand.getShortCommandUsage(), COMMAND_COLOR);
        String description = deuSubCommand.getDescription();
        msg = msg.hoverEvent(HoverEvent.showText(
                Component.text(deuSubCommand.getCommandUsage(), NamedTextColor.YELLOW)
                        .appendNewline()
                        .append(Component.text(description, NamedTextColor.AQUA))));
        if (deuSubCommand.isUnsafe()) msg = UNSAFE.append(msg);
        sender.sendMessage(msg);
    }

    public static void sendCMD(CommandSender sender, String command, String description){
        Component msg = Component.text(command, COMMAND_COLOR);
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(Component.text(description, NamedTextColor.AQUA)));
        }
        sender.sendMessage(msg);
    }

    public static void sendCMD(CommandSender sender, String command, String description, String extraInfo){
        Component msg = Component.text(command, COMMAND_COLOR);
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(
                    Component.text(description, NamedTextColor.AQUA)
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text(extraInfo, NamedTextColor.YELLOW))));
        }
        sender.sendMessage(msg);
    }

    public static void tryAddEntityToGroup(Player player, String[] args, Entity entity, DEUSubCommand subCommand, String flag){
        String entityTypeName = entity.getType().getKey().getKey();
        if (subCommand.getOptionalArguments(player, args).hasFlag(flag)){
            ActiveGroup<?> group = DEUUser.getOrCreateUser(player).getSelectedGroup();
            if (group == null) {
                player.sendMessage(Component.text("- You must have a group selected to add the "+entityTypeName+" to a group", NamedTextColor.YELLOW));
                return;
            }

            ActivePart part = group.addEntity(entity);
            if (part == null){
                player.sendMessage(Component.text("- Failed to add the "+entityTypeName+" to your selected group", NamedTextColor.YELLOW));
                return;
            }
            ((MultiPartSelection<?>) DisplayGroupManager.getPartSelection(player)).refresh();

            player.sendMessage(Component.text("- The "+entityTypeName+" has been added to your selected group", NamedTextColor.GRAY));
        }
    }

    public static Axis getPivotAxis(String arg, Player player) {
        if (arg.equalsIgnoreCase("x")){
            return Axis.X;
        }
        else if (arg.equalsIgnoreCase("y")){
            return Axis.Y;
        }
        else if (arg.equalsIgnoreCase("z")){
            return Axis.Z;
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid pivot axis!", NamedTextColor.RED)));
            return null;
        }
    }

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

    public static Collection<SpawnedDisplayAnimationFrame> getFrames(CommandSender sender, String arg, SpawnedDisplayAnimation animation) throws IllegalArgumentException{
        //Single Frame ID in arg
        try{
            int index = Integer.parseInt(arg);
            return Set.of(animation.getFrame(index));
        }
        catch(NumberFormatException ignored){}
        catch(IndexOutOfBoundsException e){ //Single Frame ID Out of Bounds
            sender.sendMessage(Component.text("Invalid Frame ID(s) or Frame Tag", NamedTextColor.RED));
            throw new IllegalArgumentException();
        }

        if (arg.equalsIgnoreCase("-all")){
            if (!animation.hasFrames()){
                sender.sendMessage(Component.text("Your selected animation has no frames!", NamedTextColor.RED));
                throw new IllegalArgumentException();
            }
            return animation.getFrames();
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
            if (frames.isEmpty()) sender.sendMessage(Component.text("Your selected animation has no frames!", NamedTextColor.RED));
            return frames;
        }
        //Single Frame Tag
        catch (IllegalArgumentException ex){
            if (!DisplayUtils.isValidTag(arg)){
                DisplayEntityPluginCommand.invalidTag(sender, arg);
                throw ex;
            }

            Set<SpawnedDisplayAnimationFrame> frames = new HashSet<>();
            for (SpawnedDisplayAnimationFrame frame : animation.getFrames()){
                if (arg.equals(frame.getTag())){
                    frames.add(frame);
                }
            }
            if (frames.isEmpty()) sender.sendMessage(Component.text("Your selected animation has no frames!", NamedTextColor.RED));
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
