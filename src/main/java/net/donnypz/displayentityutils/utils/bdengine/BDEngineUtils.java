package net.donnypz.displayentityutils.utils.bdengine;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

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
}
