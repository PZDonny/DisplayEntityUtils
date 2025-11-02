package net.donnypz.displayentityutils.listeners.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@ApiStatus.Internal
public final class DEUPlayerConnectionListener implements Listener {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/PZDonny/DisplayEntityUtils/releases/latest";
    private static final String LATEST_VERSION_URL = "https://github.com/PZDonny/DisplayEntityUtils/releases/latest";

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        //Version Check
        if (!player.hasPermission("deu.help")) return;
        DisplayAPI.getScheduler().runAsync(() -> {
            String currentVersion = DisplayAPI.getPlugin().getPluginMeta().getVersion();
            String latestVersion;
            try{
                latestVersion = getLatest();
            }
            catch(Exception ex){
                latestVersion = null;
            }
            if (latestVersion == null){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Could not perform version check.", NamedTextColor.RED)));
                player.sendMessage(Component.text("| GitHub has issues or your server has no internet connection", NamedTextColor.GRAY));
                return;
            }

            if (!compareVersions(player, currentVersion, latestVersion)){
                player.sendMessage(DisplayAPI.pluginPrefix
                        .append(Component.text("New version available!", NamedTextColor.GREEN))
                        .append(Component.text(" [DOWNLOAD v"+latestVersion+"]", NamedTextColor.GREEN))
                        .clickEvent(ClickEvent.openUrl(LATEST_VERSION_URL)));
            }
        });

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();

        DEUUser user = DEUUser.getUser(player);
        if (user != null){
            user.remove();
        }
    }

    private String getLatest() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(15))
                .uri(new URI(GITHUB_API_URL))
                .GET()
                .build();

        try (HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build()) {
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
                return obj.get("tag_name").getAsString();
            }
        }
        return null;
    }

    //-1 = v1 is prev ver
    //0 = same ver
    //1 = v1 is dev version
    static boolean compareVersions(Player player, String v1, String v2) {
        int vPart1 = 0;
        int vPart2 = 0;

        // loop until both String are processed
        for (int i1 = 0, i2 = 0; (i1 < v1.length() || i2 < v2.length());) {

            //Get Version Number 1 Section
            while (i1 < v1.length() && v1.charAt(i1) != '.') {
                char c = v1.charAt(i1);
                if (Character.isDigit(c)){
                    vPart1 = vPart1 * 10
                            + (c - '0');
                }
                else{
                    devVersion(player);
                    return true;
                }
                i1++;
            }

            //Get Version Number 2 Section
            while (i2 < v2.length() && v2.charAt(i2) != '.') {
                char c = v2.charAt(i2);
                if (Character.isDigit(c)){
                    vPart2 = vPart2 * 10
                            + (c - '0');
                }
                else{
                    devVersion(player);
                    return true;
                }
                i2++;
            }

            if (vPart1 > vPart2) //V1 newer than V2
                return true;
            if (vPart2 > vPart1) //V2 newer than V1
                return false;

            //Reset
            vPart1 = vPart2 = 0;
            i1++;
            i2++;
        }
        return true; //Same Ver
    }

    private static void devVersion(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are using a dev plugin version, a new version may be available.", NamedTextColor.LIGHT_PURPLE)));
    }
}
