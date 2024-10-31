package net.donnypz.displayentityutils.utils.deu;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class DEUCommandUtils {

    private static final HashMap<UUID, Set<ParticleDisplay>> particleDisplays = new HashMap<>();

    @ApiStatus.Internal
    public static void spawnParticleDisplays(SpawnedDisplayEntityGroup group, Player player, SpawnedDisplayAnimationFrame frame, boolean isStartParticle){
        if (isViewingParticleDisplays(player)) {
            player.sendMessage(Component.text("You are already viewing frame particles!!", NamedTextColor.RED));
            player.sendMessage(Component.text("Run \"/mdis anim cancelparticles\" to stop viewing particles", NamedTextColor.YELLOW));
            return;
        }

        if (isStartParticle && !frame.hasFrameStartParticles()){
            player.sendMessage(Component.text("Failed to view particles! The frame does not have any START particles!", NamedTextColor.RED));
            return;
        }
        else if (!isStartParticle && !frame.hasFrameEndParticles()){
            player.sendMessage(Component.text("Failed to view particles! The frame does not have any END particles!", NamedTextColor.RED));
            return;
        }

        Set<ParticleDisplay> displays = new HashSet<>();
        List<AnimationParticle> particles = isStartParticle ? frame.getFrameStartParticles() : frame.getFrameEndParticles();

        for (AnimationParticle particle : particles){
            Location spawnLoc = particle.getSpawnLocation(group);
            ParticleDisplay pd = new ParticleDisplay(spawnLoc, particle, frame, isStartParticle);
            displays.add(pd);
            pd.reveal(player);
        }
        particleDisplays.put(player.getUniqueId(), displays);
        player.sendMessage(Component.text("Run \"/mdis anim cancelparticles\" to stop viewing particles", NamedTextColor.YELLOW));
    }

    /**
     * Remove the visual representation of particles for animation frames,
     * after a player performs "/mdis anim frameinfo" then selects a prompt to view start/end particles
     * @param player
     */
    public static void removeParticleDisplays(Player player){
        Set<ParticleDisplay> displays = particleDisplays.remove(player.getUniqueId());
        if (displays != null){
            for (ParticleDisplay d : displays){
                d.remove();
            }
        }
    }

    /**
     * Check if a player has the visual representation of particles for animation frames.
     * The particles are visiable after a player performs "/mdis anim frameinfo" then selects a prompt to view start/end particles
     * @param player
     */
    public static boolean isViewingParticleDisplays(Player player){
        return particleDisplays.containsKey(player.getUniqueId());
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
            if (mainHand.getType().isBlock()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your held item cannot be a block!", NamedTextColor.RED)));
                return null;
            }
            itemStack = mainHand.clone();
        }


        //Item-ID
        else{
            Material material = Material.matchMaterial(item.toLowerCase());
            if (material == null || material.isBlock()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Item not recognized! The item's name might have been misspelled, the item doesn't exist or the item is a block.", NamedTextColor.RED)));
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
}
