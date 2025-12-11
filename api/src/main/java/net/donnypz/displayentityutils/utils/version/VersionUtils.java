package net.donnypz.displayentityutils.utils.version;

import io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
import net.donnypz.displayentityutils.DisplayAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public final class VersionUtils {

    public static boolean IS_1_20_4 = Bukkit.getUnsafe().getProtocolVersion() >= 765;
    public static boolean IS_1_20_5 = Bukkit.getUnsafe().getProtocolVersion() >= 766;
    public static boolean IS_1_21 = Bukkit.getUnsafe().getProtocolVersion() >= 767;
    public static boolean IS_1_21_2 = Bukkit.getUnsafe().getProtocolVersion() >= 768;
    public static boolean IS_1_21_5 = Bukkit.getUnsafe().getProtocolVersion() >= 770;
    public static boolean IS_1_21_6 = Bukkit.getUnsafe().getProtocolVersion() >= 771;
    public static boolean IS_1_21_7 = Bukkit.getUnsafe().getProtocolVersion() >= 772;
    public static boolean IS_1_21_9 = Bukkit.getUnsafe().getProtocolVersion() >= 773;

    public static boolean canViewDialogs(Player player, boolean sendErrorMessage){
        if (!serverHasDialogs()){
            if (sendErrorMessage){
                player.sendMessage(DisplayAPI.pluginPrefix
                        .append(Component.text("This server's version is below 1.21.7 and does not support Dialog Menus!", NamedTextColor.RED)));
            }
            return false;
        }
        else if (getProtocolVersion(player) < 771){ //1.21.6
            if (!serverHasDialogs()){
                if (sendErrorMessage){
                    player.sendMessage(DisplayAPI.pluginPrefix
                            .append(Component.text("You can only view Dialog Menus when playing on version 1.21.6 or higher!", NamedTextColor.RED)));
                }
            }
            return false;
        }
        return true;
    }

    private static int getProtocolVersion(Player player){
        if (DisplayAPI.isViaVerInstalled()){
            return ViaVersionUtil.getProtocolVersion(player);
        }
        else{
            return player.getProtocolVersion();
        }
    }

    public static Material getMaterial(@NotNull BlockType blockType){
        return Registry.MATERIAL.get(NamespacedKey.minecraft(blockType.key().asMinimalString()));
    }

    public static Material getMaterial(ItemType itemType){
        return Registry.MATERIAL.get(NamespacedKey.minecraft(itemType.key().asMinimalString()));
    }

    public static boolean hasBlockAndItemRegistry(){
        return IS_1_20_5;
    }

    public static Particle getEntityEffectParticle(){
        if (IS_1_20_5){
            return Particle.valueOf("ENTITY_EFFECT");
        }
        else{
            return Particle.valueOf("SPELL_MOB");
        }
    }

    public static Particle getItemParticle(){
        if (IS_1_20_5){
            return Particle.valueOf("ITEM");
        }
        else{
            return Particle.valueOf("ITEM_CRACK");
        }
    }

    public static Sound getSound(String soundName){
        if (soundName == null) return null;
        return Registry.SOUNDS.get(NamespacedKey.minecraft(soundName));
    }

    public static boolean serverHasDialogs(){ //Dialog API came to Paper in 1.21.7, Dialog System came to MC in 1.21.6
        return IS_1_21_7;
    }

    public static BlockData createBlockData(@NotNull String data){
        try{
            return Bukkit.createBlockData(data);
        }
        catch(IllegalArgumentException e){
            return Bukkit.createBlockData(BlockDataMapper.updateBlockData(data));
        }
    }
}
