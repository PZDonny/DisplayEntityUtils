package net.donnypz.displayentityutils.utils.bdengine;

import com.google.gson.Gson;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEModel;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEngineReader;
import org.apache.commons.codec.binary.Base64InputStream;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.zip.GZIPInputStream;

public final class BDEngineUtils {

    private static final Gson gson = new Gson();
    private static final int timeoutTime = 15;

    private BDEngineUtils(){}

    /**
     * Request a model from BDEngine's website.
     * This method is blocking and should be run asynchronously.
     * @param modelID the model ID from BDEngine
     * @return a {@link BDEngineModelResult}
     *
     * @throws InterruptedException if the operation is interrupted
     * @throws IOException if an I/ O error occurs when sending or receiving, or the client has shut down
     * @throws URISyntaxException if the API url for BDEngine is incorrect
     */
    public static BDEngineModelResult requestModel(int modelID) throws IOException, InterruptedException, URISyntaxException {
        String url = "https://block-display.com/api/?type=getModel&id="+modelID;

        HttpRequest getRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(timeoutTime))
                .uri(new URI(url))
                .GET()
                .build();

        try (HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeoutTime)).build()) {
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 400){
                BDEngineError error = gson.fromJson(response.body(), BDEngineError.class);
                throw new IOException(error.getError());
            }

            return gson.fromJson(response.body(), BDEngineModelResult.class);
        }
    }

    private static String fileExtension(String fileName){
        return fileName.endsWith(".bdengine") ? fileName : fileName+".bdengine";
    }

    /**
     * Read a saved project's model from BDEngine
     * @param file the project file
     * @param groupTag the group tag to set for the DisplayEntityUtils group created after conversion
     * @param animationPrefix the prefix to apply to all converted animations
     * @return a {@link BDEModel}
     */
    public static BDEModel readFile(@NotNull File file, @NotNull String groupTag, @NotNull String animationPrefix) {
        try(FileInputStream stream = new FileInputStream(file)) {
            return readFile(stream, groupTag, animationPrefix);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Read a saved project's model from BDEngine
     * @param plugin the plugin containing the project file
     * @param resourcePath the project's resource path
     * @param groupTag the group tag to set for the DisplayEntityUtils group created after conversion
     * @param animationPrefix the prefix to apply to all converted animations
     * @return a {@link BDEModel}
     */
    public static BDEModel readFile(@NotNull JavaPlugin plugin, @NotNull String resourcePath, @NotNull String groupTag, @NotNull String animationPrefix) {
        try(InputStream modelStream = plugin.getResource(resourcePath)){
            if (modelStream == null) return null;
            return readFile(modelStream, groupTag, animationPrefix);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Read a saved project from BDEngine and convert its model and animations to this plugin's format
     * @param inputStream the input stream containing the file
     * @param groupTag the group tag to set for the DisplayEntityUtils group created after conversion
     * @param animationPrefix the prefix to apply to all converted animations
     * @return a {@link BDEModel}
     */
    public static BDEModel readFile(@NotNull InputStream inputStream, @NotNull String groupTag, @NotNull String animationPrefix) {
        try(Base64InputStream stream64 = new Base64InputStream(inputStream);
            GZIPInputStream gzipInputStream = new GZIPInputStream(stream64);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream))
        ) {
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            return BDEngineReader.readJson(builder.toString(), groupTag, animationPrefix);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
