package net.donnypz.displayentityutils.utils.DisplayEntities;


import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

final class InteractionEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 99L;

    private ArrayList<String> partTags = new ArrayList<>(); //Legacy Part Tags (Using scoreboard Only)
    Vector3f vector;
    UUID partUUID;
    float height;
    float width;
    private byte[] persistentDataContainer = null;
    boolean isResponsive;

    InteractionEntity(Interaction interaction){
        //partTags = LegacyUtils.getLegacyPartTags(interaction);

        this.height = interaction.getInteractionHeight();
        this.width = interaction.getInteractionWidth();
        this.isResponsive = interaction.isResponsive();
        this.vector = DisplayUtils.getInteractionTranslation(interaction).toVector3f();
        this.partUUID = DisplayUtils.getPartUUID(interaction);

        try{
            persistentDataContainer = interaction.getPersistentDataContainer().serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    Interaction createEntity(Location location, GroupSpawnSettings settings){
        return location.getWorld().spawn(location, Interaction.class, spawn ->{
            spawn.setInteractionHeight(height);
            spawn.setInteractionWidth(width);
            spawn.setResponsive(isResponsive);
            for (String partTag : partTags){
                spawn.addScoreboardTag(partTag);
            }

            if (persistentDataContainer != null){
                try{
                    spawn.getPersistentDataContainer().readFromBytes(persistentDataContainer);
                }
                catch(IOException ignore){}
            }

            if (partUUID != null){
                spawn.getPersistentDataContainer().set(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
            }

            settings.apply(spawn);
        });
    }

     Vector getVector(){
        return Vector.fromJOML(vector);
    }

    ArrayList<String> getLegacyPartTags() {
        return partTags;
    }
    
    float getHeight() {
        return height;
    }

    float getWidth() {
        return width;
    }

    boolean isReponsive(){
        return isResponsive;
    }
}
