package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Settings that can be applied to {@link DisplayEntityGroup#spawn(Location, GroupSpawnedEvent.SpawnReason, GroupSpawnSettings)}
 * to customize the spawn result of the resulting {@link SpawnedDisplayEntityGroup}.
 */
public class GroupSpawnSettings {

    int teleportationDuration = 0;
    HashMap<String, Set<UUID>> hiddenPartTags = new HashMap<>(); //Tag , Player UUIDs
    HashMap<String, Display.Brightness> brightness = new HashMap<>();
    HashMap<String, Display.Billboard> billboard = new HashMap<>();
    boolean visibleByDefault = true;
    Set<UUID> visiblePlayers = new HashSet<>();
    boolean hideInteractions = false;

    /**
     * Set the teleportation duration for all display entities that will be spawned from {@link DisplayEntityGroup#spawn(Location, GroupSpawnedEvent.SpawnReason, GroupSpawnSettings)}.
     * This has no effect on Interaction entities.
     * @param teleportationDuration how long display entities should interpolate when teleporting, in ticks
     * @return this
     */
    public GroupSpawnSettings setTeleportationDuration(int teleportationDuration) {
        this.teleportationDuration = teleportationDuration;
        return this;
    }

    /**
     * Set the brightness for display entities that will be spawned from {@link DisplayEntityGroup#spawn(Location, GroupSpawnedEvent.SpawnReason, GroupSpawnSettings)}.
     * This has no effect on Interaction entities.
     * @param brightness The brightness to set
     * @param partTag The part tag that entities should have for the brightness to be applied. A null partTag will apply the brightness to all display entities.
     * If an entity has a part tag that is being added to this, it will be prioritized over the null partTag brightness.
     * @return this
     */
    public GroupSpawnSettings addBrightness(@Nullable Display.Brightness brightness, @Nullable String partTag){
        this.brightness.put(partTag, brightness);
        return this;
    }

    /**
     * Set the billboard for display entities that will be spawned from {@link DisplayEntityGroup#spawn(Location, GroupSpawnedEvent.SpawnReason, GroupSpawnSettings)}.
     * This has no effect on Interaction entities.
     * @param billboard The billboard to set
     * @param partTag The part tag that entities should have for the billboard to be applied. A null partTag will apply the billboard to all display entities.
     * If an entity has a part tag that is being added to this, it will be prioritized over the null partTag billboard.
     * @return this
     */
    public GroupSpawnSettings addBillboard(@NotNull Display.Billboard billboard, @Nullable String partTag){
        this.billboard.put(partTag, billboard);
        return this;
    }

    /**
     * Add a part tag that, if contained within an entity, will hide the entity
     * @param partTag the part tag
     * @return this
     */
    public GroupSpawnSettings addHiddenPartTag(String partTag, @Nullable Collection<Player> visiblePlayers){

        Set<UUID> players = new HashSet<>();
        if (visiblePlayers != null && !visiblePlayers.isEmpty()){
            visiblePlayers.forEach(player -> {
                players.add(player.getUniqueId());
            });
        }
        hiddenPartTags.put(partTag, players);
        return this;
    }

    /**
     * Determine if the {@link SpawnedDisplayEntityGroup} will be visible by default when spawned
     * @param visible the visibility
     * @param visiblePlayers the players that can see the group even if visibility is false
     * @return this
     */
    public GroupSpawnSettings visibleByDefault(boolean visible, @Nullable Collection<Player> visiblePlayers){
        this.visibleByDefault = visible;
        if (visiblePlayers != null && !visiblePlayers.isEmpty()){
            visiblePlayers.forEach(player -> {
                this.visiblePlayers.add(player.getUniqueId());
            });
        }

        return this;
    }

    /**
     * Determine if the Interaction entities spawned will be visible by default when spawned
     * @param hideInteractions whether interactions should be hidden
     * @return this
     */
    public GroupSpawnSettings hideInteractions(boolean hideInteractions) {
        this.hideInteractions = hideInteractions;
        return this;
    }

    void apply(Display display){
    //Determine Visibility
        if (!visibleByDefault){
            display.setVisibleByDefault(false);
            if (!visiblePlayers.isEmpty()){ //Reveal for players
                for (UUID uuid : visiblePlayers){
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()){
                        player.showEntity(DisplayEntityPlugin.getInstance(), display);
                    }
                }
            }
        }
        else{
            if (hiddenPartTags.isEmpty()){
                display.setVisibleByDefault(true);
            }
            else{
                determineVisibleByDefault(display);
            }
        }



    //Teleport Duration
        display.setTeleportDuration(teleportationDuration);

    //Brightness
        if (!brightness.isEmpty()){
            Display.Brightness b = brightness.get(null);
            if (b != null){
                display.setBrightness(b);
            }

            for (String tag : brightness.keySet()){
                if (DisplayUtils.hasTag(display, tag)){
                    display.setBrightness(brightness.get(tag));
                    break;
                }
            }
        }

    //Billboard
        if (!billboard.isEmpty()){
            Display.Billboard b = billboard.get(null);
            if (b != null){
                display.setBillboard(b);
            }

            for (String tag : billboard.keySet()){
                if (DisplayUtils.hasTag(display, tag)){
                    display.setBillboard(billboard.get(tag));
                    break;
                }
            }
        }
    }

    void apply(Interaction interaction){
        //Determine Visibility
        if (!visibleByDefault || hideInteractions){
            interaction.setVisibleByDefault(false);
            if (!visiblePlayers.isEmpty()){ //Reveal for players
                for (UUID uuid : visiblePlayers){
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()){
                        player.showEntity(DisplayEntityPlugin.getInstance(), interaction);
                    }
                }
            }
        }
        else{
            if (hiddenPartTags.isEmpty()){
                interaction.setVisibleByDefault(true);
            }
            else{
                determineVisibleByDefault(interaction);
            }
        }


    }

    private void determineVisibleByDefault(Display display){
        for (String tag : hiddenPartTags.keySet()){
            if (DisplayUtils.hasTag(display, tag)){
                display.setVisibleByDefault(false);
                reveal(display, tag);
                break;
            }
        }
    }

    private void determineVisibleByDefault(Interaction interaction){
        for (String tag : hiddenPartTags.keySet()){
            if (DisplayUtils.hasTag(interaction, tag)){
                interaction.setVisibleByDefault(false);
                reveal(interaction, tag);
                break;
            }
        }
    }

    private void reveal(Entity entity, String tag){
        Set<UUID> visiblePlayers = hiddenPartTags.get(tag);
        for (UUID uuid : visiblePlayers){
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()){
                player.showEntity(DisplayEntityPlugin.getInstance(), entity);
            }
        }
    }
}
