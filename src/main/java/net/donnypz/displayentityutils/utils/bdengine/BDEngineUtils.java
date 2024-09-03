package net.donnypz.displayentityutils.utils.bdengine;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class BDEngineUtils {

    static final Gson gson = new Gson();

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
                .uri(new URI(url))
                .GET()
                .build();
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 400){
                BDEngineError error = gson.fromJson(response.body(), BDEngineError.class);
                throw new RuntimeException(error.getError());
            }

            BDEngineModelResult result = gson.fromJson(response.body(), BDEngineModelResult.class);
            return result;
        }
    }
}
