package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;

import java.util.ArrayList;

final class LegacyUtils {

    /**
     * Gets the part tag of a Display Entity
     * @param display Display Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    static ArrayList<String> getLegacyPartTags(Display display){
        return getLegacyPartTags(display, false);
    }

    /**
     * Gets the part tag of an Interaction Entity
     * @param interaction Interaction Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    static ArrayList<String> getLegacyPartTags(Interaction interaction){
        return getLegacyPartTags(interaction, false);
    }

    /**
     * Gets the part tag of a Display Entity without the Part Tag Prefix
     * @param display Display Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    static ArrayList<String> getCleanPartTags(Display display){
        return getLegacyPartTags(display, true);
    }

    /**
     * Gets the part tag of an Interaction Entity without the Part Tag Prefix
     * @param interaction Interaction Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    static ArrayList<String> getCleanPartTags(Interaction interaction){
        return getLegacyPartTags(interaction, true);
    }

    private static ArrayList<String> getLegacyPartTags(Entity entity, boolean clean){
        ArrayList<String> tags = new ArrayList<>();
        for (String s : entity.getScoreboardTags()){
            if (s.contains(DisplayEntityPlugin.getLegacyPartTagPrefix())){
                if (clean){
                    tags.add(s.replace(DisplayEntityPlugin.getLegacyPartTagPrefix(), ""));
                }
                else{
                    tags.add(s);
                }

            }
        }
        return tags;
    }
}
