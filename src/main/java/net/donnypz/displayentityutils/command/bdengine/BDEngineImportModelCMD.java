package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineModelResult;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;


class BDEngineImportModelCMD extends PlayerSubCommand {

    BDEngineImportModelCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("importmodel", parentSubCommand, Permission.BDENGINE_SPAWN_MODEL);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis bdengine importmodel <model-id>", NamedTextColor.RED)));
            return;
        }
        Location spawnLoc = player.getLocation();
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            try{
                int modelID = Integer.parseInt(args[2]);
                player.sendMessage(Component.text("Retrieving Model...", NamedTextColor.GRAY));
                BDEngineModelResult result = BDEngineUtils.requestModel(modelID);
                if (result == null){
                    throw new InterruptedException("Null Result");
                }

                Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> {
                    if (!result.spawn(spawnLoc)){
                        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to spawn model! The spawn location's chunk is not loaded or the model was uploaded as a BDEngine file on the website (Spawning Commands not found), and that format is not supported!", NamedTextColor.RED)));
                        return;
                    }
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Attempted to spawn model at your location!", NamedTextColor.GREEN)));

                    if (result.getCommandCount() > 1){
                        player.sendMessage(Component.text("! The model resulted in creating multiple groups!", NamedTextColor.RED));
                        player.sendMessage(Component.text("Select the group then use \"/mdis group merge\" to merge the produced group", NamedTextColor.GRAY, TextDecoration.ITALIC));
                        player.sendMessage(Component.text("It is not recommended to import groups that use animations this way!", NamedTextColor.RED, TextDecoration.ITALIC));
                    }

                    player.sendMessage(Component.text("\n- If your model did NOT spawn, the model was uploaded as a BDEngine file on the website, and that format is not supported.\n", NamedTextColor.GRAY));
                });

            }
            catch(NumberFormatException e){
                player.sendMessage(Component.text("Enter a valid Model ID! This can be found on the page of a model on BDEngine. " +
                        "Look for \"ID for API\" and enter the number provided", NamedTextColor.RED));
            }
            catch(InterruptedException | IOException | URISyntaxException e){
                player.sendMessage(Component.text("An error occurred when attempting to retrieve the BDEngine model!", NamedTextColor.RED));
                e.printStackTrace();
            }
            catch(RuntimeException e){
                player.sendMessage(Component.text("An error occurred when attempting to retrieve the BDEngine model!", NamedTextColor.RED));
                player.sendMessage(Component.text("Error: "+e.getMessage(), NamedTextColor.GRAY, TextDecoration.ITALIC));
                e.printStackTrace();
            }
        });
    }
}
