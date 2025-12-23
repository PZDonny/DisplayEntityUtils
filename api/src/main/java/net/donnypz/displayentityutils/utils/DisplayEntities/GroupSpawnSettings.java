package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
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
    boolean persistentByDefault = DisplayConfig.defaultPersistence();
    boolean persistenceOverride = DisplayConfig.overrideByDefault();
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
     * Add a part tag that, if contained within a part, will hide the part's entity
     * @param partTag the part tag
     * @return this
     */
    public GroupSpawnSettings addHiddenPartTag(@NotNull String partTag, @Nullable Collection<Player> visiblePlayers){

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
     * Determine if an {@link ActiveGroup} will be persistent by default when spawned
     * @param persistentByDefault
     * @return
     */
    public GroupSpawnSettings persistentByDefault(boolean persistentByDefault){
        this.persistentByDefault = persistentByDefault;
        return this;
    }

    /**
     * Determine if a {@link SpawnedDisplayEntityGroup} can have its persistence overriden when loaded by a chunk, based on config values
     * @param allowPersistenceOverride
     * @return
     */
    public GroupSpawnSettings allowPersistenceOverride(boolean allowPersistenceOverride){
        persistenceOverride = allowPersistenceOverride;
        return this;
    }

    /**
     * Determine if a {@link SpawnedDisplayEntityGroup} will be visible by default when spawned
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
    public GroupSpawnSettings hideInteractionsByDefault(boolean hideInteractions) {
        this.hideInteractions = hideInteractions;
        return this;
    }

    boolean applyVisibility(PacketDisplayEntityPart part, Player player){
        if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            if (hideInteractions) return false;
        }

        if (!visibleByDefault){
            return visiblePlayers.contains(player.getUniqueId());
        }
        else{
            for (Map.Entry<String, Set<UUID>> entry : hiddenPartTags.entrySet()){
                String tag = entry.getKey();
                Set<UUID> hideForPlayers = entry.getValue();
                if (part.partTags.contains(tag) && hideForPlayers.contains(player.getUniqueId())) {
                    return false;
                }
            }
            return true;
        }
    }

    boolean applyAttributes(PacketDisplayEntityPart part){
        if (part.isDisplay()){
            //Teleport Duration
            part.attributeContainer.setAttribute(DisplayAttributes.TELEPORTATION_DURATION, teleportationDuration);

            //Brightness
            if (!brightness.isEmpty()){
                Display.Brightness b = brightness.get(null);
                if (b != null){
                    part.attributeContainer.setAttribute(DisplayAttributes.BRIGHTNESS, b);
                }

                for (Map.Entry<String, Display.Brightness> entry : brightness.entrySet()){
                    String tag = entry.getKey();
                    if (tag == null) continue;
                    Display.Brightness brightness = entry.getValue();
                    if (part.partTags.contains(tag)){
                        part.attributeContainer.setAttribute(DisplayAttributes.BRIGHTNESS, brightness);
                        break;
                    }
                }
            }

            //Billboard
            if (!billboard.isEmpty()){
                Display.Billboard b = billboard.get(null);
                if (b != null){
                    part.attributeContainer.setAttribute(DisplayAttributes.BILLBOARD, b);
                }

                for (Map.Entry<String, Display.Billboard> entry : billboard.entrySet()){
                    String tag = entry.getKey();
                    if (tag == null) continue;
                    Display.Billboard bb = entry.getValue();
                    if (part.partTags.contains(tag)){
                        part.attributeContainer.setAttribute(DisplayAttributes.BILLBOARD, bb);
                        break;
                    }
                }
            }
        }
        return true;
    }


    void apply(Display display){
    //Determine Visibility
        if (!visibleByDefault){
            display.setVisibleByDefault(false);
            if (!visiblePlayers.isEmpty()){ //Reveal for players
                for (UUID uuid : visiblePlayers){
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()){
                        player.showEntity(DisplayAPI.getPlugin(), display);
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

            for (Map.Entry<String, Display.Brightness> entry : brightness.entrySet()){
                String tag = entry.getKey();
                if (tag == null) continue;
                Display.Brightness br = entry.getValue();
                if (DisplayUtils.hasPartTag(display, tag)){
                    display.setBrightness(br);
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

            for (Map.Entry<String, Display.Billboard> entry : billboard.entrySet()){
                String tag = entry.getKey();
                if (tag == null) continue;
                Display.Billboard bb = entry.getValue();
                if (DisplayUtils.hasPartTag(display, tag)){
                    display.setBillboard(bb);
                    break;
                }
            }
        }
        display.setPersistent(persistentByDefault);
    }

    void apply(Interaction interaction){
        //Determine Visibility
        if (!visibleByDefault || hideInteractions){
            interaction.setVisibleByDefault(false);
            if (!visiblePlayers.isEmpty()){ //Reveal for players
                for (UUID uuid : visiblePlayers){
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()){
                        player.showEntity(DisplayAPI.getPlugin(), interaction);
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
        interaction.setPersistent(persistentByDefault);
    }

    void apply(Mannequin mannequin){
        //Determine Visibility
        if (!visibleByDefault){
            mannequin.setVisibleByDefault(false);
            if (!visiblePlayers.isEmpty()){ //Reveal for players
                for (UUID uuid : visiblePlayers){
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()){
                        player.showEntity(DisplayAPI.getPlugin(), mannequin);
                    }
                }
            }
        }
        else{
            if (hiddenPartTags.isEmpty()){
                mannequin.setVisibleByDefault(true);
            }
            else{
                determineVisibleByDefault(mannequin);
            }
        }
    }


    private void determineVisibleByDefault(Entity entity){
        for (String tag : hiddenPartTags.keySet()){
            if (DisplayUtils.hasPartTag(entity, tag)){
                entity.setVisibleByDefault(false);
                reveal(entity, tag);
                break;
            }
        }
    }

    private void reveal(Entity entity, String tag){
        Set<UUID> visiblePlayers = hiddenPartTags.get(tag);
        for (UUID uuid : visiblePlayers){
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()){
                player.showEntity(DisplayAPI.getPlugin(), entity);
            }
        }
    }
}
