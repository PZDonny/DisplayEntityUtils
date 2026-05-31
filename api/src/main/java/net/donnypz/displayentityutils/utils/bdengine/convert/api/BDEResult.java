package net.donnypz.displayentityutils.utils.bdengine.convert.api;

import com.google.gson.*;
import net.donnypz.displayentityutils.events.BDEAPIConvertEvent;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The result of a BDEngine Request after using {@link BDEngineUtils#importProject(int)}
 * No commands will be provided if the model was uploaded to BDEngine as a BDEngine Project File.
 */
public final class BDEResult {

    private final String version; //game_version
    private final String type;
    final String projectId;
    final String passengers;
    final BDEResultDatapack datapack;


    BDEResult(String version,
              String type,
              String projectId,
              String passengers,
              BDEResultDatapack datapack) {
        this.version = version;
        this.type = type;
        this.projectId = projectId;
        this.passengers = passengers;
        this.datapack = datapack;
    }

    public static BDEResult create(@NotNull String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonObject content = Util.getObject(root, "content");

        String version = Util.getString(content, "version");
        String type = Util.getString(content, "type");
        String projectId = Util.getString(content, "project_id");

        JsonArray passengers = Util.getArray(content, "passengers");
        String passengerData = null;

        if (passengers != null && !passengers.isEmpty()) {
            JsonElement first = passengers.get(0);

            if (first != null && !first.isJsonNull()) {
                passengerData = first.getAsString();
            }
        }

        JsonObject datapack = Util.getObject(content, "datapack");

        return new BDEResult(
                version,
                type,
                projectId,
                passengerData,
                datapack == null ? null : BDEResultDatapack.create(datapack)
        );
    }

    /**
     * Convert the retrieved BDEngine project into a group/model & animation format, usable for DisplayEntityUtils.
     * <br>The {@link BDEAPIConvertEvent} will be called after successful conversion completion.
     * <br><b>This method should be run synchronously.</b>
     * @param spawnLocation       where the conversion should take place. This should be in a loaded chunk
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public void convert(@NotNull Location spawnLocation, @NotNull String groupSaveTag, @NotNull String animationSavePrefix, boolean saveGroup, boolean saveAnimations, boolean despawnAfter) {
        this.convert(null, null, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    /**
     * Convert the retrieved BDEngine project into a group/model & animation format, usable for DisplayEntityUtils.
     * <br>The {@link BDEAPIConvertEvent} will be called after successful conversion completion.
     * <br><b>This method should be run synchronously.</b>
     * @param player              the player involved in the conversion. typically supplied when using conversion commands
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public void convert(@NotNull Player player, @NotNull String groupSaveTag, @NotNull String animationSavePrefix, boolean saveGroup, boolean saveAnimations, boolean despawnAfter) {
        this.convert(null, player, player.getLocation(), groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    /**
     * Convert the retrieved BDEngine project into a group/model & animation format, usable for DisplayEntityUtils.
     * <br>The {@link BDEAPIConvertEvent} will be called after successful conversion completion.
     * <br><b>This method should be run synchronously.</b>
     * @param conversionId        the id used to reference this conversion later through events.
     * @param player              the player involved in the conversion. typically supplied when using conversion commands
     * @param spawnLocation       where the conversion should take place. This should be in a loaded chunk
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public void convert(@Nullable String conversionId,
                           @Nullable Player player,
                           @NotNull Location spawnLocation,
                           @NotNull String groupSaveTag,
                           @NotNull String animationSavePrefix,
                           boolean saveGroup,
                           boolean saveAnimations,
                           boolean despawnAfter) {
        new BDEAPIConverter(
                this,
                conversionId,
                player,
                spawnLocation,
                groupSaveTag,
                animationSavePrefix,
                saveGroup,
                saveAnimations,
                despawnAfter
        );
    }
}
