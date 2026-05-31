package net.donnypz.displayentityutils.utils.bdengine.convert.api;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.bdengine.convert.common.BDECommandConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

class BDEAPIConverter extends BDECommandConverter {

    private final BDEResult result;
    private final BDEResultDatapack datapack;

    BDEAPIConverter(@NotNull BDEResult result, @Nullable String conversionId, @NotNull Location spawnLocation, @NotNull String groupSaveTag, @NotNull String animationSavePrefix, boolean saveGroup, boolean saveAnimations, boolean despawnAfter) {
        this(result, conversionId, null, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    BDEAPIConverter(@NotNull BDEResult result, @NotNull Player player, @NotNull String groupSaveTag, @NotNull String animationSavePrefix, boolean saveGroup, boolean saveAnimations, boolean despawnAfter) {
        this(result, null, player, player.getLocation(), groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    BDEAPIConverter(@NotNull BDEResult result, @Nullable String conversionId, @Nullable Player player, @NotNull Location spawnLocation, @NotNull String groupSaveTag, @NotNull String animationSavePrefix, boolean saveGroup, boolean saveAnimations, boolean despawnAfter) {
        super(conversionId, player, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
        this.result = result;
        this.datapack = result.datapack;
        spawnModel();
    }

    private void spawnModel(){
        String nbt = String.format("{Passengers:[%s],Tags:[\"%s\"]}", result.passengers, getSubMasterScoreboardTag());
        String command = "summon block_display ~ ~ ~ "+nbt;
        super.executeCommandAsMasterEntity(command);
    }

    @Override
    protected void onConversionCompleted() {}

    @Override
    protected Collection<String> getAnimationNames() {
        return datapack != null ? datapack.animations.keySet() : List.of();
    }

    @Override
    protected int getFrameCount(@NotNull String animationName) {
        if (datapack == null) return 0;
        BDEResultAnimation anim = datapack.animations.get(animationName);
        return anim == null ? 0 : anim.frames.size();
    }

    @Override
    protected int getLastFrameId(@NotNull String animationName) {
        if (datapack == null) return 0;
        BDEResultAnimation anim = datapack.animations.get(animationName);
        return anim == null ? 0 : anim.getLastFrame();
    }

    @Override
    protected SpawnedDisplayAnimationFrame executeFrameCommands(String animationName, int frameId, int lastAddedFrameId) {
        BDEResultAnimation anim = datapack.animations.get(animationName);
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(Math.max(0, (frameId-lastAddedFrameId-1)*2), 2);

        BDEResultAnimationFrame bdeFrame = anim.frames.get(frameId);
        if (bdeFrame == null || bdeFrame.isEmpty()){
            return null;
        }

        if (bdeFrame.hasTransforms()){
            for (String cmd : bdeFrame.transformCommands){
                try{
                    super.executeFrameCommand(frame, cmd);
                }
                catch(CommandException e){
                    super.sendMessage(Component.text("Animation conversion failed! Read console"));
                    throw new RuntimeException("Failed to execute command from API Project: "+result.projectId, e);
                }
            }
        }
        return frame;
    }
}
