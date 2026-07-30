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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class VersionUtils {

    public static final boolean IS_1_20_4 = Bukkit.getUnsafe().getProtocolVersion() >= 765;
    public static final boolean IS_1_20_5 = Bukkit.getUnsafe().getProtocolVersion() >= 766;
    public static final boolean IS_1_21 = Bukkit.getUnsafe().getProtocolVersion() >= 767;
    public static final boolean IS_1_21_2 = Bukkit.getUnsafe().getProtocolVersion() >= 768;
    public static final boolean IS_1_21_5 = Bukkit.getUnsafe().getProtocolVersion() >= 770;
    public static final boolean IS_1_21_6 = Bukkit.getUnsafe().getProtocolVersion() >= 771;
    public static final boolean IS_1_21_7 = Bukkit.getUnsafe().getProtocolVersion() >= 772;
    public static final boolean IS_1_21_9 = Bukkit.getUnsafe().getProtocolVersion() >= 773;
    public static final boolean IS_1_21_11 = Bukkit.getUnsafe().getProtocolVersion() >= 774;
    public static final boolean IS_26_1 = Bukkit.getUnsafe().getProtocolVersion() >= 775;
    public static final boolean IS_26_2 = Bukkit.getUnsafe().getProtocolVersion() >= 776;

    public static final boolean IS_DEV_VERSION;
    private static Particle ENTITY_EFFECT_PARTICLE;
    private static Particle ITEM_PARTICLE;

    static{
        String pluginVer = DisplayAPI.getPlugin().getPluginMeta().getVersion();
        IS_DEV_VERSION = isDevVersion(pluginVer);
        setParticles();
    }

    private VersionUtils(){}

    public static @NotNull String getPluginVersion(){
        return DisplayAPI.getPlugin().getPluginMeta().getVersion();
    }

    public static @NotNull String getCleanPluginVersion(){
        return cleanVersionString(getPluginVersion());
    }

    public static boolean isDevVersion(){
        return IS_DEV_VERSION;
    }

    @ApiStatus.Internal
    public static boolean isDevVersion(@NotNull String version){
        String clean = cleanVersionString(version);
        return !version.equals(clean);
    }

    @ApiStatus.Internal
    public static @NotNull String cleanVersionString(@NotNull String version){
        return version.replaceAll("[^0-9.]", "");
    }

    public static boolean isCurrentOlderThan(@NotNull String comparedVersion){
        String current = getPluginVersion();
        String cleanCurrent = VersionUtils.cleanVersionString(current);
        String cleanComparedVersion = VersionUtils.cleanVersionString(comparedVersion);

        String[] currentArr = cleanCurrent.split("\\.");
        String[] comparedArr = cleanComparedVersion.split("\\.");

        int length = Math.max(currentArr.length, comparedArr.length);

        for (int i = 0; i < length; i++){
            int num1 = i < currentArr.length ? Integer.parseInt(currentArr[i]) : 0;
            int num2 = i < comparedArr.length ? Integer.parseInt(comparedArr[i]) : 0;

            //behind
            if (num2 > num1) return true;
            if (num1 > num2) return false;
        }
        return false;
    }

    public static boolean canViewDialogs(@NotNull Player player, boolean sendErrorMessage){
        if (!serverHasDialogs()){
            if (sendErrorMessage){
                player.sendMessage(DisplayAPI.pluginPrefix
                        .append(Component.text("This server's version is below 1.21.7 and does not support Dialog Menus!", NamedTextColor.RED)));
            }
            return false;
        }
        return true;
    }

    public static boolean canSpawnMannequins(){
        return IS_1_21_9;
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

    public static boolean hasSpears(){
        return IS_1_21_11;
    }

    private static void setParticles(){
        if (IS_1_20_5){
            ENTITY_EFFECT_PARTICLE = Particle.valueOf("ENTITY_EFFECT");
            ITEM_PARTICLE = Particle.valueOf("ITEM");

        }
        else{
            ENTITY_EFFECT_PARTICLE = Particle.valueOf("SPELL_MOB");
            ITEM_PARTICLE = Particle.valueOf("ITEM_CRACK");
        }
    }

    public static Particle getEntityEffectParticle(){
        return ENTITY_EFFECT_PARTICLE;
    }

    public static Particle getItemParticle(){
        return ITEM_PARTICLE;
    }

    public static Sound getSound(String soundName){
        if (soundName == null) return null;
        String[] split = soundName.split(":");
        if (split.length < 2){
            return Registry.SOUNDS.get(NamespacedKey.minecraft(soundName));
        }
        else{
            String namespace = split[0];
            String key = split[1];
            return Registry.SOUNDS.get(new NamespacedKey(namespace, key));
        }
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
