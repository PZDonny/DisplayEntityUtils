package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class DEUUser {
    static final HashMap<UUID, DEUUser> users = new HashMap<>();
    private final UUID userUUID;
    private SpawnedDisplayEntityGroup selectedGroup;
    private SpawnedDisplayAnimation selectedAnimation;
    SpawnedPartSelection selectedPartSelection;
    private AnimationParticleBuilder particleBuilder;
    private boolean isValid = true;


    private DEUUser(Player player){
        userUUID = player.getUniqueId();
        users.put(userUUID, this);
    }

    public static @NotNull DEUUser getOrCreateUser(@NotNull Player player){
        DEUUser user = getUser(player);
        return Objects.requireNonNullElseGet(user, () -> new DEUUser(player));
    }

    public static @Nullable DEUUser getUser(@NotNull Player player){
        return getUser(player.getUniqueId());
    }

    public static @Nullable DEUUser getUser(@NotNull UUID uuid){
        return users.get(uuid);
    }

    /**
     * Set the selected {@link SpawnedDisplayEntityGroup} of a user to the specified group
     *
     * @param spawnedDisplayEntityGroup SpawnedDisplayEntityGroup to be set to the player
     * @return false if {@link DisplayEntityPlugin#limitGroupSelections()} is true and a player already has the group selected
     */
    public boolean setSelectedSpawnedGroup(@NotNull SpawnedDisplayEntityGroup spawnedDisplayEntityGroup) {
        for (DEUUser user : DEUUser.users.values()){
            if (user.getSelectedGroup() == spawnedDisplayEntityGroup){
                return user.userUUID == userUUID;
            }
        }
        setSelectedPartSelection(new SpawnedPartSelection(spawnedDisplayEntityGroup),true);
        return true;
    }

    /**
     * Set a user's {@link SpawnedPartSelection} and their group to the part's group
     *
     * @param selection The selection for the user to have selected
     * @param setGroup Whether to set the user's selected group to the selection's group
     */
    public void setSelectedPartSelection(@NotNull SpawnedPartSelection selection, boolean setGroup) {
        selectedPartSelection = selection;
        if (setGroup) {
            selectedGroup = selection.getGroup();
        }
    }

    /**
     * Set the selected {@link SpawnedDisplayAnimation} of a user to the specified animation
     * @param spawnedDisplayAnimation the animation the user should have selected
     */
    public void setSelectedSpawnedAnimation(@NotNull SpawnedDisplayAnimation spawnedDisplayAnimation){
        selectedAnimation = spawnedDisplayAnimation;
    }

    public void setAnimationParticleBuilder(@NotNull AnimationParticleBuilder particleBuilder){
        this.particleBuilder = particleBuilder;
    }

    /**
     * Remove a user's {@link SpawnedDisplayEntityGroup} selection
     */
    public void deselectSpawnedGroup() {
        selectedGroup = null;
        DEUCommandUtils.removeRelativePoints(Bukkit.getPlayer(userUUID));
    }

    /**
     * Unset this user's selected {@link SpawnedDisplayAnimation}
     */
    public void deselectSpawnedAnimation(){
        selectedAnimation = null;
    }

    /**
     * Remove this user's {@link AnimationParticleBuilder}
     */
    public void removeAnimationParticleBuilder(){
        if (particleBuilder != null){
            particleBuilder.remove();
            particleBuilder = null;
        }
    }

    public @Nullable SpawnedDisplayEntityGroup getSelectedGroup(){
        return selectedGroup;
    }

    public @Nullable SpawnedPartSelection getSelectedPartSelection(){
        return selectedPartSelection;
    }

    public @Nullable SpawnedDisplayAnimation getSelectedAnimation(){
        return selectedAnimation;
    }

    public boolean isPartSelectionValid(){
        return selectedPartSelection != null && selectedPartSelection.isValid();
    }

    public @Nullable AnimationParticleBuilder getAnimationParticleBuilder(){
        return particleBuilder;
    }

    public @NotNull UUID getUserUUID(){
        return userUUID;
    }

    public boolean isValid() {
        return isValid;
    }

    @ApiStatus.Internal
    public void remove(){
        isValid = false;
        selectedGroup = null;
        if (selectedPartSelection != null) selectedPartSelection.remove();
        if (particleBuilder != null) particleBuilder.remove();

        Player player = Bukkit.getPlayer(userUUID);
        if (player != null){
            DEUCommandUtils.removeRelativePoints(player);
        }
    }
}
