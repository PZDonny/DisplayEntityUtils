package net.donnypz.displayentityutils.listeners.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class DEUPlayerConnectionListener implements Listener {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/PZDonny/DisplayEntityUtils/releases/latest";
    private static final String LATEST_VERSION_URL = "https://github.com/PZDonny/DisplayEntityUtils/releases/latest";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();


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
                compareVersions(player, currentVersion, latestVersion);
            }
            catch(IOException | InterruptedException ex){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Could not perform version check.", NamedTextColor.RED)));
                player.sendMessage(Component.text("| GitHub is experiencing issue or your server has no internet connection", NamedTextColor.GRAY));
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
        RelativePointUtils.removeRelativePoints(player);
    }

    private String getLatest() throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(15))
                .uri(URI.create(GITHUB_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(getRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("GitHub API error: " + response.statusCode() + " - " + response.body());
        }

        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!obj.has("tag_name")) {
            throw new IOException("Invalid GitHub response: missing tag_name");
        }
        return obj.get("tag_name").getAsString();
    }

    void compareVersions(Player player, String current, String latest){
        String cleanCurrent = current.replaceAll("[^0-9.]", "");

        boolean isDevVersion = !current.equals(cleanCurrent);

        String[] a = cleanCurrent.split("\\.");
        String[] b = latest.split("\\.");

        int length = Math.max(a.length, b.length);

        for (int i = 0; i < length; i++){
            int num1 = i < a.length ? Integer.parseInt(a[i]) : 0;
            int num2 = i < b.length ? Integer.parseInt(b[i]) : 0;

            if (num2 > num1){ //behind
                if (isDevVersion){
                    sendNewVersionAvailableOnDev(player, latest);
                }
                else{
                    sendNewVersionAvailable(player, latest);
                }
                return;
            }
        }

        String cleanLatest = latest.replaceAll("[^0-9.]", "");
        if (!cleanCurrent.equals(cleanLatest)){
            sendLatestOnDev(player, latest);
        }
    }

    private void sendNewVersionAvailable(Player player, String latestVersion){
        player.sendMessage(DisplayAPI.pluginPrefix
                .append(Component.text("New version available!", NamedTextColor.GREEN))
                .append(Component.text(" [DOWNLOAD v"+latestVersion+"]", NamedTextColor.GREEN))
                .clickEvent(ClickEvent.openUrl(LATEST_VERSION_URL)));
    }

    private void sendNewVersionAvailableOnDev(Player player, String latestVersion){
        player.sendMessage(DisplayAPI.pluginPrefixLong);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>| <light_purple>You are using a dev/pre-release plugin version. Unexpected issues may occur."));
        player.sendMessage(MiniMessage
                .miniMessage()
                .deserialize("<gray>| <green>New Version available!:")
                .append(Component.text(" [DOWNLOAD v"+latestVersion+"]", NamedTextColor.GREEN))
                .clickEvent(ClickEvent.openUrl(LATEST_VERSION_URL)));
    }

    private void sendLatestOnDev(Player player, String latestVersion){
        player.sendMessage(DisplayAPI.pluginPrefixLong);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>| <light_purple>You are using a dev/pre-release plugin version. Unexpected issues may occur."));
        player.sendMessage(MiniMessage
                .miniMessage()
                .deserialize("<gray>| <yellow>Latest release:")
                .append(Component.text(" [DOWNLOAD v"+latestVersion+"]", NamedTextColor.GREEN))
                .clickEvent(ClickEvent.openUrl(LATEST_VERSION_URL)));
    }
}
