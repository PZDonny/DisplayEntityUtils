package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@ApiStatus.Internal
public final class LegacyUtils {

    /**
     * Gets the legacy part tags of a Display Entity
     * @param display Display Entity to retrieve the tag from
     * @param clean whether to get the tags without the legacy prefix
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static ArrayList<String> getLegacyPartTags(Display display, boolean clean){
        return getLegacyPartTags((Entity) display, clean);
    }

    /**
     * Gets the legacy part tags of an Interaction Entity
     * @param interaction Interaction Entity to retrieve the tag from
     * @param clean whether to get the tags without the legacy prefix
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static ArrayList<String> getLegacyPartTags(Interaction interaction, boolean clean){
        return getLegacyPartTags((Entity) interaction, clean);
    }

    /**
     * Get a part tag without the legacy part tag prefix prepended
     * @param partTag the legacy part tag
     * @return a string without the legacy part tag prefix
     */
    public static String stripLegacyPartTagPrefix(@NotNull String partTag){
        return partTag.replace(DisplayEntityPlugin.getLegacyPartTagPrefix(), "");
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
