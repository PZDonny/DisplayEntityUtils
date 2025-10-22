package net.donnypz.displayentityutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ConversionUtils {

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

    public static Color getColorFromText(@NotNull String color){
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

    public static byte getOpacityAsByte(float input){ //0-1
        int opacity = Math.round(input * 255);

        if (opacity >= 4 && opacity <= 26) { //Adjusted for Minecraft Shader Values (Rendering is discarded)
            opacity = 25;
        }

        if (opacity > 127) { //Outside of byte range
            opacity -= 256;
        }

        return (byte) opacity;
    }

    public static String getCoordinateString(@NotNull Location location){
        return round(location.x())+" "+round(location.y())+" "+round(location.z());
    }

    private static double round(double coord){
        return Math.round(coord * 100)/100.0;
    }

    public static String getExecuteCommandWorldName(@NotNull World w){
        String worldName;
        if (w.equals(Bukkit.getWorlds().getFirst())){
            worldName = "overworld";
        }
        else{
            worldName = w.getName();
        }
        return worldName;
    }

    /**
     * Get a chunk key from a chunk's x and z coordinates
     * @param x chunk's x coordinate
     * @param z chunk's z coordinate
     * @return a long, the chunk's key
     */
    public static long getChunkKey(int x, int z){
        return ((long) z << 32) | (x & 0xFFFFFFFFL); //Order is inverted
    }

    public static long getChunkKey(@NotNull Location location){
        int chunkX = location.blockX() >> 4;
        int chunkZ = location.blockZ() >> 4;
        return getChunkKey(chunkX, chunkZ);
    }

    /**
     * Get a chunk's x and z coordinates from a chunk's key
     * @param chunkKey chunk's key
     * @return a int array containing the x and z coordinates, respectively
     */
    public static int[] getChunkCoordinates(long chunkKey){
        int z = (int) (chunkKey >> 32);
        int x = (int) chunkKey;
        return new int[]{x, z};
    }
}
