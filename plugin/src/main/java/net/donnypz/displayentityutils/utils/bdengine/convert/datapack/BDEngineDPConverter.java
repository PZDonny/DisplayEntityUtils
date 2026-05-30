package net.donnypz.displayentityutils.utils.bdengine.convert.datapack;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.listeners.bdengine.BDEngineConversionListener;
import net.donnypz.displayentityutils.managers.*;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.bdengine.convert.common.BDECommandConverter;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BDEngineDPConverter extends BDECommandConverter {

    public static final String CONVERT_DELETE_SUB_PARENT_TAG = "deu_delete_sub_parent";
    public static final String CONVERSION_SCOREBOARD_PREFIX = "deu_dp_conversion_";

    static final String FUNCTION_FOLDER = VersionUtils.IS_1_21 ? "function" : "functions";
    private static final String CREATE_MODEL_PATH = "/create.mcfunction";
    private final LinkedHashMap<String, ArrayList<ZipEntry>> animations = new LinkedHashMap<>();
    private final ZipFile zipFile;


    public BDEngineDPConverter(@NotNull String datapackName,
                               @Nullable String conversionId,
                               @NotNull Location spawnLocation,
                               @NotNull String groupSaveTag,
                               @NotNull String animationSavePrefix,
                               boolean saveGroup,
                               boolean saveAnimations,
                               boolean despawnAfter) {
        this(datapackName, conversionId, null, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    public BDEngineDPConverter(@NotNull String datapackName,
                               @Nullable Player player,
                               @NotNull String groupSaveTag,
                               @NotNull String animationSavePrefix,
                               boolean saveGroup,
                               boolean saveAnimations,
                               boolean despawnAfter) {
        this(datapackName, null, player, player.getLocation(), groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

    public BDEngineDPConverter(@NotNull String datapackName,
                               @Nullable String conversionId,
                               @Nullable Player player,
                               @NotNull Location spawnLocation,
                               @NotNull String groupSaveTag,
                               @NotNull String animationSavePrefix,
                               boolean saveGroup,
                               boolean saveAnimations,
                               boolean despawnAfter) {
        super(conversionId, player, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);

        if (!datapackName.endsWith(".zip")){
            datapackName = datapackName+".zip";
        }

        ZipFile zip;
        try{
            zip = new ZipFile(PluginFolders.animDatapackFolder+"/"+datapackName);
        }
        catch (IOException e) {
            this.zipFile = null;
            super.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("Failed to find datapack with the provided name! Ensure that the datapack is placed in the \"bdenginedatapacks\" folder of this plugin, as a .zip file", NamedTextColor.RED)));
            return;
        }

        this.zipFile = zip;
        searchEntries();
    }

    private void spawnModel(ZipEntry createModelEntry){
        //Set projectName from entry
        if (projectName == null){
            // data/{projectName}/functions
            String projectName = createModelEntry.getName().split("/"+ FUNCTION_FOLDER +"/")[0].substring(5);
            this.setProjectName(projectName);
        }

        executeCommands(createModelEntry);
    }

    private void searchEntries(){
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            //Spawn model
            if (entryName.endsWith(CREATE_MODEL_PATH)){
                spawnModel(entry);
            }
            //Add Animation
            else if (isAnimationEntry(entryName)){ //Add animation
                animations.putIfAbsent(getAnimationName(entryName, "a"), new ArrayList<>());
            }
            //Add Animation Frame
            else if (!isSoundPath(entryName) && entryName.contains("/keyframe_") && entryName.endsWith(".mcfunction")){
                addFrame(entry);
            }
        }

        //Save Model and Animations
        DisplayAPI.getScheduler().runLater(this::saveAndConvert, 1);
    }

    private void addFrame(ZipEntry entry){
        String animName = getAnimationName(entry.getName(), "k");
        ArrayList<ZipEntry> frames = animations.computeIfAbsent(animName, k -> new ArrayList<>());
        frames.add(entry);
    }

    private void saveAndConvert(){
        SpawnedDisplayEntityGroup createdGroup = BDEngineConversionListener.removeCreatedGroup(masterEntityUUID);
        if (createdGroup == null){
            super.sendMessage(Component.text("Failed to find model/group created from datapack!", NamedTextColor.RED));
            super.sendMessage(Component.text("| The datapack may have been generated for a different game version or of an older BDEngine format", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return;
        }

        createdGroup.setTag(super.getGroupSaveTag());
        createdGroup.seedPartUUIDs(SpawnedDisplayEntityGroup.DEFAULT_PART_UUID_SEED);

        super.sendMessage(Component.empty());

        int delay = 0;
        for (String animName : animations.sequencedKeySet()){
            List<ZipEntry> frames = animations.get(animName);
            DisplayAPI.getScheduler().runLater(() -> {
                super.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Converting Animation: <yellow>"+animName));
                processAnimation(createdGroup, animName);
            }, delay);
            delay+=(frames.size()*2);
        }

        //Despawn group after animation conversions
        DisplayAPI.getScheduler().runLater(() -> {
            if (saveGroup){
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.LOCAL, createdGroup.toDisplayEntityGroup(), player);
                super.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Group Tag: <yellow>"+createdGroup.getTag()));
            }
            else{
                super.sendMessage(Component.text("| The group was not saved.", NamedTextColor.GRAY));
            }

            if (despawnAfter){
                DisplayAPI.getScheduler().run(() -> createdGroup.unregister(true, true));
            }

            try{
                zipFile.close();
            }
            catch(IOException ignored){}
        }, delay+2);
    }


    private void executeCommands(ZipEntry zipEntry){
        try(InputStream stream = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
            String line;
            while ((line = br.readLine()) != null){
                if (line.startsWith("#") || line.isEmpty()){
                    continue;
                }

                if (zipEntry.getName().endsWith("/check_loop.mcfunction") || zipEntry.getName().contains("/check_pause_")){
                    continue;
                }

                //Summon Model from datapack
                if (zipEntry.getName().endsWith(CREATE_MODEL_PATH)){
                    //Camera
                    if (line.startsWith("execute unless") || line.startsWith("data")) {
                        continue;
                    }

                    //Parent/Master/Root or Camera Spawning
                    if (line.startsWith("summon block_display") ){
                        continue;
                    }

                    //Sub-Master Parts
                    else if (line.contains("@s")){
                        line = line.split("@s run ", 2)[1];
                        line = line.substring(0, line.length()-2)+",\""+CONVERSION_SCOREBOARD_PREFIX+masterEntityUUID+"\"]}";
                    }
                    if (!line.startsWith("ride")) {
                        line = line.substring(0, line.length()-2)+",\""+CONVERT_DELETE_SUB_PARENT_TAG+"\"]}";
                    }
                    else{
                        line = line.split("mount")[0]+"mount "+masterEntityUUID;
                    }
                }

                else if (line.startsWith("schedule")) {
                    continue;
                }

                String coordinates = ConversionUtils.getCoordinateString(SPAWN_LOCATION);
                World w = SPAWN_LOCATION.getWorld();
                String worldName = ConversionUtils.getExecuteCommandWorldName(w);

                String finalCommand = String.format(
                        "execute at %s positioned %s in %s run %s",
                        masterEntityUUID,
                        coordinates,
                        worldName,
                        line
                );
                try{
                    Bukkit.dispatchCommand(SILENT_SENDER, finalCommand);
                }
                catch(CommandException e){
                    super.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Animation conversion failed! Error in console...", NamedTextColor.RED)));
                    super.sendMessage(Component.text("| Is your datapack for the correct server version?", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    throw new RuntimeException("Failed to read command", e);
                }

            }
        }
        catch (IOException e){
            super.sendMessage(Component.text("Animation conversion failed! Read console"));
            throw new RuntimeException("Failed to execute command from ZipEntry: "+zipEntry.getName(), e);
        }
    }

    private boolean isAnimationEntry(String entryName){
        return !entryName.endsWith(".mcfunction") && (entryName.contains("/"+ FUNCTION_FOLDER +"/a/") && !entryName.endsWith("/"+ FUNCTION_FOLDER +"/a/"));
    }

    private boolean isSoundPath(String entryName){
        return entryName.contains("/"+ FUNCTION_FOLDER +"/k_s");
    }

    private String getAnimationName(String entryName, String folder){
        String animName = entryName.split(FUNCTION_FOLDER +"/"+folder+"/")[1];
        return animName.endsWith(".mcfunction") ? animName.split("/")[0] : animName.substring(0, animName.length()-1);
    }

    @Override
    protected int getFrameCount(@NotNull String animationName) {
        List<ZipEntry> entries = animations.get(animationName);
        return entries == null ? 0 : entries.size();
    }

    @Override
    protected SpawnedDisplayAnimationFrame executeFrameCommands(String animationName, int frameId) {
        List<ZipEntry> frames = animations.get(animationName);
        ZipEntry zipEntry = frames.get(frameId);
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 2);

        try(InputStream stream = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream))){

            String line;
            while ((line = br.readLine()) != null){
                super.executeFrameCommand(frame, line);
            }
            return frame;
        }
        catch (IOException e){
            super.sendMessage(Component.text("Animation conversion failed! Read console"));
            throw new RuntimeException("Failed to execute command from ZipEntry: "+zipEntry.getName(), e);
        }
    }
}
