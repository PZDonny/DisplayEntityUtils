package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.bdengine.convert.api.BDEResult;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;


class BDEngineImportCMD extends PlayerSubCommand {

    BDEngineImportCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("import", parentSubCommand, Permission.BDENGINE_IMPORT);
        setTabComplete(2, "<project-id>");
        setTabComplete(3, "<group-tag-to-set>");
        setTabComplete(4, "<anim-tag-prefix-to-set>");
        addFlag(BDEngineCMD.DESPAWN_FLAG);
        addFlag(BDEngineCMD.ADAPT_TAGS_FLAG);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) {
            player.sendMessage(Component.text("Use \"-\" for a tag if you do not want to save the group/animation(s)", NamedTextColor.GRAY));
            return;
        }
        Location spawnLoc = player.getLocation();
        DisplayAPI.getScheduler().runAsync(() -> {
            try{
                int modelID = Integer.parseInt(args[2]);
                String groupTag = args[3];
                String animPrefix = args[4];
                boolean saveGroups = !groupTag.equals("-");
                boolean saveAnimations = !animPrefix.equals("-");
                player.sendMessage(Component.text("Retrieving Model...", NamedTextColor.GRAY));
                BDEResult result = BDEngineUtils.importProject(modelID);
                if (result == null){
                    throw new InterruptedException("Null Result");
                }

                OptionalArguments oArgs = getOptionalArguments(player, args);
                DisplayAPI.getScheduler().run(() -> {
                    result.convert(null,
                            player,
                            spawnLoc,
                            !saveGroups ? "" : groupTag,
                            !saveAnimations ? "" : animPrefix,
                            saveGroups,
                            saveAnimations,
                            oArgs.hasFlag(BDEngineCMD.DESPAWN_FLAG),
                            oArgs.hasFlag(BDEngineCMD.ADAPT_TAGS_FLAG)
                    );
                    player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Attempting to import BDEngine project...", NamedTextColor.YELLOW)));
                });
            }
            catch(NumberFormatException e){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid project export ID!", NamedTextColor.RED)));
            }
            catch(IOException e){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed retrieve BDEngine project. Import ID was never generated or has expired.", NamedTextColor.RED)));
            }
            catch(InterruptedException | URISyntaxException e){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("An error occurred when attempting to retrieve the BDEngine model! See console.", NamedTextColor.RED)));
                e.printStackTrace();
            }
        });
    }

    @Override
    protected String getDescription() {
        return """
               Import and convert a BDEngine project's model and animations into your game world
               
               "-despawn" will despawn the group/model after importing
               
               "-adapttags" will adapt scoreboard tags into part tags (tags DEU uses to identify entities/parts)
               """;
    }
}
