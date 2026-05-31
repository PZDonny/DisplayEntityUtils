package net.donnypz.displayentityutils.utils.bdengine;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.BDEDatapackConvertEvent;
import net.donnypz.displayentityutils.utils.bdengine.convert.api.BDEResult;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEModel;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEngineReader;
import org.apache.commons.codec.binary.Base64InputStream;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.zip.GZIPInputStream;

public final class BDEngineUtils {

    private static final Gson GSON = new Gson();
    private static final int TIMEOUT_TIME = 15;
    private static final String BDENGINE_URL = "https://block-display.com/server-api/?id=";
    private static final HttpClient HTTP_CLIENT = HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_TIME))
            .build();

    private BDEngineUtils(){}

    /**
     * Import a BDEngine project with an "Export to server" export id supplied from BDEngine.
     * <br><b>This method is blocking and should be run asynchronously.</b>
     * @param exportID the model ID from BDEngine
     * @return a {@link BDEResult}
     *
     * @throws InterruptedException if the operation is interrupted
     * @throws IOException if an I/O error occurs when sending or receiving, or the client has shut down
     * @throws URISyntaxException if the API url for BDEngine is incorrect
     */
    public static BDEResult importProject(int exportID) throws IOException, InterruptedException, URISyntaxException {
        String url = BDENGINE_URL+exportID;

        HttpRequest getRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(TIMEOUT_TIME))
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(getRequest, HttpResponse.BodyHandlers.ofString());

        //Check for error this way since an error returns a 200 status code regardless
        try{
            BDEngineError error = GSON.fromJson(response.body(), BDEngineError.class);
            if (error.getError() != null) throw new IOException(error.getError());
        }
        catch(JsonSyntaxException e){}

        return BDEResult.create(response.body());
    }

    /**
     * Convert a BDEngine datapack project into a group/model & animation format, usable for DisplayEntityUtils.
     * <br>The {@link BDEDatapackConvertEvent} will be called after successful conversion completion.
     * <br><b>This method should be run synchronously.</b>
     * @param datapackName        the name of the datapack to be converted
     * @param spawnLocation       where the conversion should take place. This should be in a loaded chunk
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public static void convertDatapack(@NotNull String datapackName,
                                 @NotNull Location spawnLocation,
                                 @NotNull String groupSaveTag,
                                 @NotNull String animationSavePrefix,
                                 boolean saveGroup,
                                 boolean saveAnimations,
                                 boolean despawnAfter){
        convertDatapack(datapackName, null, null, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    /**
     * Convert a BDEngine datapack project into a group/model & animation format, usable for DisplayEntityUtils.
     * <br>The {@link BDEDatapackConvertEvent} will be called after successful conversion completion.
     * <br><b>This method should be run synchronously.</b>
     * @param datapackName        the name of the datapack to be converted
     * @param player              the player involved in the conversion. typically supplied when using conversion commands
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public static void convertDatapack(@NotNull String datapackName,
                                 @NotNull Player player,
                                 @NotNull String groupSaveTag,
                                 @NotNull String animationSavePrefix,
                                 boolean saveGroup,
                                 boolean saveAnimations,
                                 boolean despawnAfter){
        convertDatapack(datapackName, null, player, player.getLocation(), groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    /**
     * Convert a BDEngine datapack project into a group/model & animation format, usable for DisplayEntityUtils.
     * <br>The {@link BDEDatapackConvertEvent} will be called after successful conversion completion.
     * <br><b>This method should be run synchronously.</b>
     * @param datapackName        the name of the datapack to be converted
     * @param conversionId        the id used to reference this conversion later through events.
     * @param player              the player involved in the conversion. typically supplied when using conversion commands
     * @param spawnLocation       where the conversion should take place. This should be in a loaded chunk
     * @param groupSaveTag        the group's tag
     * @param animationSavePrefix the prefix for animation tags
     * @param saveGroup           whether the created group should be saved
     * @param saveAnimations      whether created animations should be saved
     * @param despawnAfter        whether the created group should be despawned after conversion
     */
    public static void convertDatapack(@NotNull String datapackName,
                         @Nullable String conversionId,
                         @Nullable Player player,
                         @NotNull Location spawnLocation,
                         @NotNull String groupSaveTag,
                         @NotNull String animationSavePrefix,
                         boolean saveGroup,
                         boolean saveAnimations,
                         boolean despawnAfter){
        DisplayAPI.getBDEConversionHandler().convertDatapack(
                datapackName,
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

    /**
     * Read a saved project's model from a ".bdengine" file's contents
      * @param file the project file
     * @return a {@link BDEModel}
     */
    public static BDEModel readFile(@NotNull File file) {
        try(FileInputStream stream = new FileInputStream(file)) {
            return readFile(stream);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Read a saved project's model from a ".bdengine" file's contents
     * @param plugin the plugin containing the project file
     * @param resourcePath the project's resource path
     * @return a {@link BDEModel}
     */
    public static BDEModel readFile(@NotNull JavaPlugin plugin, @NotNull String resourcePath) {
        try(InputStream modelStream = plugin.getResource(resourcePath)){
            if (modelStream == null) return null;
            return readFile(modelStream);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Read a saved project from a ".bdengine" file's contents
     * @param inputStream the input stream containing the file's data
     * @return a {@link BDEModel}
     */
    public static BDEModel readFile(@NotNull InputStream inputStream) {
        try(Base64InputStream stream64 = new Base64InputStream(inputStream);
            GZIPInputStream gzipInputStream = new GZIPInputStream(stream64);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream))
        ) {
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            return BDEngineReader.readJson(builder.toString());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
