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
import java.util.List;
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

    InteractionEntity(Interaction interaction){
        //partTags = LegacyUtils.getLegacyPartTags(interaction);

        height = interaction.getInteractionHeight();
        width = interaction.getInteractionWidth();
        this.vector = DisplayUtils.getInteractionTranslation(interaction).toVector3f();
        this.partUUID = DisplayUtils.getPartUUID(interaction);

        try{
            persistentDataContainer = interaction.getPersistentDataContainer().serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    Interaction createEntity(Location location, boolean isVisible){
        return location.getWorld().spawn(location, Interaction.class, spawn ->{
            spawn.setInteractionHeight(height);
            spawn.setInteractionWidth(width);
            spawn.setVisibleByDefault(isVisible);
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
        });
    }

    Interaction createEntity(Location location, List<String> hiddenTags){
        return location.getWorld().spawn(location, Interaction.class, spawn ->{
            spawn.setInteractionHeight(height);
            spawn.setInteractionWidth(width);

            for (String partTag : partTags){
                spawn.addScoreboardTag(partTag);
            }

            if (persistentDataContainer != null){
                try{
                    spawn.getPersistentDataContainer().readFromBytes(persistentDataContainer);
                }
                catch(IOException ignore){}
            }

            boolean visible = true;

            for (String legacy : partTags){ //Legacy Tags (Scoreboard)
                if (hiddenTags.contains(legacy)){
                    visible = false;
                }
            }

            if (visible){ //PDC Tags and not hidden from Legacy
                for (String tag : hiddenTags){
                    if (DisplayUtils.hasTag(spawn, tag)){
                        visible = false;
                        break;
                    }
                }
            }

            spawn.setVisibleByDefault(visible);

            if (partUUID != null){
                spawn.getPersistentDataContainer().set(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
            }
        });
    }

     Vector getVector(){
        return Vector.fromJOML(vector);
    }

    ArrayList<String> getLegacyPartTags() {
        return partTags;
    }

    /**
     * Get this InteractionEntity's height
     * @return InteractionEntity's height
     */
    float getHeight() {
        return height;
    }

    /**
     * Get this InteractionEntity's width
     * @return InteractionEntity's width
     */
    float getWidth() {
        return width;
    }
}
