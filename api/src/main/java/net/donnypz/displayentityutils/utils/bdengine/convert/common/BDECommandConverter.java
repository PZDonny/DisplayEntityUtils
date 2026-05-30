package net.donnypz.displayentityutils.utils.bdengine.convert.common;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public abstract class BDECommandConverter {
    private static final String FRAME_POINT_SOUND_TAG = "deu_dp_convert";
    protected static final CommandSender SILENT_SENDER = Bukkit.createCommandSender(f -> {
    });

    protected final String conversionId;
    protected final Player player;
    protected final Location SPAWN_LOCATION;
    protected final String SPAWN_COORDINATES;
    protected final String groupSaveTag;
    protected final String animationSavePrefix;
    protected final boolean saveGroup;
    protected final boolean saveAnimations;
    protected final boolean despawnAfter;


    protected final HashSet<DEUSound> bufferedSounds = new HashSet<>();
    protected final UUID masterEntityUUID;
    protected List<SpawnedDisplayAnimation> animations = new ArrayList<>();

    protected String projectName;

    /**
     * @param conversionId        the id used to reference this conversion later through events.
     * @param spawnLocation       where the conversion should take place. This should be in a loaded chunk
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public BDECommandConverter(
            @Nullable String conversionId,
            @NotNull Location spawnLocation,
            @NotNull String groupSaveTag,
            @NotNull String animationSavePrefix,
            boolean saveGroup,
            boolean saveAnimations,
            boolean despawnAfter) {
        this(conversionId, null, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    /**
     * @param player              the player involved in the conversion. typically supplied when using conversion commands
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public BDECommandConverter(
            @Nullable Player player,
            @NotNull String groupSaveTag,
            @NotNull String animationSavePrefix,
            boolean saveGroup,
            boolean saveAnimations,
            boolean despawnAfter) {
        this(null, player, player.getLocation(), groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    /**
     * @param conversionId        the id used to reference this conversion later through events.
     * @param player              the player involved in the conversion. typically supplied when using conversion commands
     * @param spawnLocation       where the conversion should take place. This should be in a loaded chunk
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public BDECommandConverter(
            @Nullable String conversionId,
            @Nullable Player player,
            @NotNull Location spawnLocation,
            @NotNull String groupSaveTag,
            @NotNull String animationSavePrefix,
            boolean saveGroup,
            boolean saveAnimations,
            boolean despawnAfter) {
        this.conversionId = conversionId;
        this.player = player;
        this.SPAWN_LOCATION = spawnLocation;
        this.SPAWN_LOCATION.setPitch(0);
        this.SPAWN_LOCATION.setYaw(0);
        this.SPAWN_COORDINATES = ConversionUtils.getCoordinateString(SPAWN_LOCATION);

        this.groupSaveTag = groupSaveTag;
        this.animationSavePrefix = animationSavePrefix;
        this.saveGroup = saveGroup;
        this.saveAnimations = saveAnimations;
        this.despawnAfter = despawnAfter;

        Display masterEntity = spawnLocation.getWorld()
                .spawn(spawnLocation, BlockDisplay.class, bd -> {
                    bd.setPersistent(false);
                });
        this.masterEntityUUID = masterEntity.getUniqueId();
        DisplayAPI.getBDEConversionHandler().createConversionGroup(masterEntity);
    }

    protected BDECommandConverter setProjectName(@NotNull String projectName) {
        this.projectName = projectName;
        return this;
    }

    protected void processAnimation(SpawnedDisplayEntityGroup createdGroup, String animationName) {
        DisplayAPI.getScheduler().partRunTimer(createdGroup.getMasterPart(), new Scheduler.SchedulerRunnable() {
            final SpawnedDisplayAnimation anim = new SpawnedDisplayAnimation();
            final int frameCount = getFrameCount(animationName);
            int i = 0;

            @Override
            public void run() {
                if (frameCount != 0) {
                    //Apply command to group entities (Transformations, Texture Values, etc.)
                    SpawnedDisplayAnimationFrame frame = executeFrameCommands(animationName, i);
                    frame.setDelay(0);
                    frame.setDuration(2);

                    //Set Frame Transformation
                    frame.setTransformation(createdGroup);

                    //Add Sounds
                    if (!bufferedSounds.isEmpty()) {
                        FramePoint point = new FramePoint(FRAME_POINT_SOUND_TAG, createdGroup, SPAWN_LOCATION);
                        for (DEUSound sound : bufferedSounds) {
                            point.addSound(sound);
                        }
                        bufferedSounds.clear();
                        frame.addFramePoint(point);
                    }

                    anim.forceAddFrame(frame);
                    i++;
                }

                if (i == frameCount) {
                    try {
                        createdGroup.setToFrame(anim, anim.getFrames().getFirst());
                    } catch (IndexOutOfBoundsException ignored) {
                    }

                    //Save
                    DisplayAPI.getScheduler().run(() -> {
                        if (animationSavePrefix.isBlank()) {
                            anim.setAnimationTag(projectName.replace(".zip", "_auto_" + animationName));
                        } else {
                            anim.setAnimationTag(animationSavePrefix + "_" + animationName);
                        }

                        if (saveAnimations){
                            boolean animationSuccess = DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim.toDisplayAnimation(), null);
                            if (animationSuccess) {
                                sendMessage(MiniMessage.miniMessage().deserialize("<green>| BDEngine Animation converted and saved! <gray>(" + anim.getAnimationTag() + ")"));
                                playSound(Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
                                animations.add(anim);
                            } else {
                                sendMessage(MiniMessage.miniMessage().deserialize("<red>| BDEngine Animation conversion failed! Save failure. <gray>(" + anim.getAnimationTag() + ")"));
                                playSound(Sound.ENTITY_SHULKER_AMBIENT, 1, 1.5f);
                            }
                        }
                        else{
                            sendMessage(MiniMessage.miniMessage().deserialize("<green>| BDEngine Animation converted! <gray>(" + anim.getAnimationTag() + ")"));
                            playSound(Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
                            animations.add(anim);
                        }
                        sendMessage(Component.empty());
                    });
                    cancel();
                }
            }
        }, 0, 2); //BDEngine Animation Frame Duration is 2 ticks
    }

    protected abstract int getFrameCount(@NotNull String animationName);

    protected String getGroupSaveTag() {
        return groupSaveTag.isBlank() ? projectName.replace(".zip", "_auto") : groupSaveTag;
    }

    protected abstract SpawnedDisplayAnimationFrame executeFrameCommands(String animationName, int frameId);

    protected void executeFrameCommand(SpawnedDisplayAnimationFrame frame, String command) {
        if (command.startsWith("#") || command.startsWith("schedule") || command.isEmpty()) {
            return;
        }

        if (command.startsWith("tp")) {
            setCameraVectorAndDirection(frame, command);
        }

        //Animation Sound
        if (command.startsWith("playsound") || (command.startsWith("execute") && command.contains("playsound"))) {
            DEUSound sound = DEUSound.fromCommand(command);
            if (sound != null) {
                bufferedSounds.add(sound);
            }
        }

        String worldName = ConversionUtils.getExecuteCommandWorldName(SPAWN_LOCATION.getWorld());
        Bukkit.dispatchCommand(SILENT_SENDER, "execute at " + masterEntityUUID + " positioned " + SPAWN_COORDINATES + " in " + worldName + " run " + command);
    }

    private void setCameraVectorAndDirection(SpawnedDisplayAnimationFrame frame, String line) {
        try {
            String argsString = line.split("_camera,limit=1,sort=nearest] ")[1];
            String[] args = argsString.split(" ");
            double x = Double.parseDouble(args[0].substring(1));
            double y = Double.parseDouble(args[1].substring(1));
            double z = Double.parseDouble(args[2].substring(1));
            double yaw = Double.parseDouble(args[3]);
            double pitch = Double.parseDouble(args[4]);
            frame.setAnimationCamera(new AnimationCamera(x, y, z, (float) yaw, (float) pitch));
            Location cameraLoc = SPAWN_LOCATION.clone().add(x, y, z);
            cameraLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH, cameraLoc, 1, 0, 0, 0, 0);
        } catch (IndexOutOfBoundsException e) {
        }
    }

    protected void sendMessage(Component component) {
        if (player != null) player.sendMessage(component);
    }

    protected void playSound(Sound sound, float volume, float pitch) {
        if (player != null) player.playSound(player, sound, volume, pitch);
    }
}
