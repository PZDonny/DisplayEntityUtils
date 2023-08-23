package com.pzdonny.displayentityutils.utils.DisplayEntities;


import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;

public final class InteractionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;

    String partTag;
    byte[] vectorFromMaster;
    float height;
    float width;

    InteractionEntity(Interaction interaction, SpawnedDisplayEntityGroup group){
        String partTag = DisplayGroupManager.getPartTag(interaction);
        if (partTag != null){
            this.partTag = partTag;
        }

        height = interaction.getInteractionHeight();
        width = interaction.getInteractionWidth();
        try{
            Vector vector = group.getMasterPart().getEntity().getLocation().toVector().subtract(interaction.getLocation().toVector());
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitStream = new BukkitObjectOutputStream(byteStream);
            bukkitStream.writeObject(vector);
            this.vectorFromMaster = byteStream.toByteArray();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    Interaction createEntity(Location location){
        return location.getWorld().spawn(location, Interaction.class, spawn ->{
           spawn.setInteractionHeight(height);
           spawn.setInteractionWidth(width);
            if (partTag != null){
                spawn.addScoreboardTag(DisplayEntityPlugin.partTagPrefix+partTag);
            }
        });
    }

     Vector getVector(){
        if (vectorFromMaster == null) return null;
        try{
            ByteArrayInputStream byteIn = new ByteArrayInputStream(vectorFromMaster);
            BukkitObjectInputStream bukkitIn = new BukkitObjectInputStream(byteIn);
            return (Vector) bukkitIn.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get this InteractionEntity's part tag
     * @return This InteractionEntity's part tag. Null if it does not have one
     */
    public String getPartTag() {
        return partTag;
    }

    /**
     * Get this InteractionEntity's height
     * @return InteractionEntity's height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Get this InteractionEntity's width
     * @return InteractionEntity's width
     */
    public float getWidth() {
        return width;
    }
}
