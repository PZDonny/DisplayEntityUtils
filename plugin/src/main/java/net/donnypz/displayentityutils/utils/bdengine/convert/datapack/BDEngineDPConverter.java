package net.donnypz.displayentityutils.utils.bdengine.convert.datapack;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.listeners.bdengine.DatapackEntitySpawned;
import net.donnypz.displayentityutils.managers.*;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BDEngineDPConverter {

    static final CommandSender silentSender = Bukkit.createCommandSender(feedback -> {});
    static final String functionFolderName = VersionUtils.IS_1_21 ? "function" : "functions";
    private static final String createModelPath = "/create.mcfunction";

    private String projectName = null;
    private final String groupSaveTag;
    private final String animationSavePrefix;
    private final LinkedHashMap<String, ArrayList<ZipEntry>> animations = new LinkedHashMap<>();

    public BDEngineDPConverter(@NotNull Player player, @NotNull String datapackName, @NotNull String groupSaveTag, @NotNull String animationSavePrefix){
        this.groupSaveTag = groupSaveTag;
        this.animationSavePrefix = animationSavePrefix;
        if (!datapackName.endsWith(".zip")){
            datapackName = datapackName+".zip";
        }
        try{
            ZipFile zipFile = new ZipFile(PluginFolders.animDatapackFolder+"/"+datapackName);
            searchEntries(player, datapackName, zipFile.entries(), zipFile);
        }
        catch (IOException e) {
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("Failed to find datapack with the provided name! Ensure that the datapack is placed in the \"bdenginedatapacks\" folder of this plugin, as a .zip file", NamedTextColor.RED)));
        }
    }

    private boolean isAnimationEntry(String entryName){
        return !entryName.endsWith(".mcfunction") && (entryName.contains("/"+ functionFolderName +"/a/") && !entryName.endsWith("/"+ functionFolderName +"/a/"));
    }

    private boolean isSoundPath(String entryName){
        return entryName.contains("/"+ functionFolderName +"/k_s");
    }

    private String getAnimationName(String entryName, String folder){
        String animName = entryName.split(functionFolderName +"/"+folder+"/")[1];
        return animName.endsWith(".mcfunction") ? animName.split("/")[0] : animName.substring(0, animName.length()-1);
    }

    private void getProjectName(String entryName){
        if (projectName == null){
            this.projectName = entryName.split("/"+functionFolderName+"/")[0].substring(5);
        }
    }

    private void spawnModel(Player player, ZipEntry createModelEntry, ZipFile zipFile){
        getProjectName(createModelEntry.getName());
        executeCommands(createModelEntry, zipFile, player, player.getLocation());
    }

    private void addFrame(ZipEntry entry){
        String animName = getAnimationName(entry.getName(), "k");
        ArrayList<ZipEntry> frames = animations.computeIfAbsent(animName, k -> new ArrayList<>());
        frames.add(entry);
    }

    private void searchEntries(Player player, String datapackName, Enumeration<? extends ZipEntry> entries, ZipFile zipFile){
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            //Summon model
            if (entryName.endsWith(createModelPath)){
                spawnModel(player, entry, zipFile);
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
        Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () ->{
            playAndSave(player, datapackName, zipFile);
        }, 30);
    }

    private void playAndSave(Player player, String datapackName, ZipFile zipFile){
        SpawnedDisplayEntityGroup createdGroup = DatapackEntitySpawned.getProjectGroup(projectName);
        if (createdGroup == null){
            player.sendMessage(Component.text("Failed to find model/group created from datapack!", NamedTextColor.RED));
            player.sendMessage(Component.text("| The datapack may be a legacy one (Before BDEngine v1.13). Try using /mdis bdengine convertdpleg", NamedTextColor.GRAY, TextDecoration.ITALIC));
            player.sendMessage(Component.text("| The datapack may have been generated for a different game version", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return;
        }

        createdGroup.setTag(groupSaveTag.isBlank() ? datapackName.replace(".zip", "_auto") : groupSaveTag);
        DatapackEntitySpawned.finalizeAnimationPreparation(projectName);
        createdGroup.seedPartUUIDs(SpawnedDisplayEntityGroup.defaultPartUUIDSeed);

        player.sendMessage(Component.empty());
        boolean save = !groupSaveTag.equals("-");

        int delay = 0;
        for (String animName : animations.sequencedKeySet()){
            List<ZipEntry> frames = animations.get(animName);
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Converting Animation: <yellow>"+animName));
                processAnimation(createdGroup, zipFile, frames, datapackName, animName, player);
            }, delay);
            delay+=(frames.size()*2);
        }

        //Despawn group after animation conversions
        Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
            player.sendMessage(Component.empty());
            if (save){
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.LOCAL, createdGroup.toDisplayEntityGroup(), player);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Group Tag: <yellow>"+createdGroup.getTag()));
            }
            else{
                player.sendMessage(Component.text("The group will not be saved due to setting the group tag to \"-\"", NamedTextColor.GRAY));
            }

            Bukkit.getScheduler().runTask(DisplayAPI.getPlugin(), () -> createdGroup.unregister(true, true));
        }, delay+5);
    }


    private void processAnimation(SpawnedDisplayEntityGroup createdGroup, ZipFile zipFile, List<ZipEntry> frames, String datapackName, String animName, Player player){
        new BukkitRunnable(){
            final SpawnedDisplayAnimation anim = new SpawnedDisplayAnimation();
            final int frameCount = frames.size();
            int i = 0;
            @Override
            public void run() {
                if (i == frameCount){
                    try{
                        createdGroup.setToFrame(anim, anim.getFrames().getFirst());
                    }
                    catch(IndexOutOfBoundsException ignored){}

                    //Save
                    Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
                        if (animationSavePrefix.isBlank()){
                            anim.setAnimationTag(datapackName.replace(".zip", "_auto_"+animName));
                        }
                        else{
                            anim.setAnimationTag(animationSavePrefix+"_"+animName);
                        }
                        boolean animationSuccess = DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim.toDisplayAnimation(), null);
                        anim.remove();

                        if (animationSuccess){
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>BDEngine Animation Converted! <gray>("+anim.getAnimationTag()+")"));
                            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
                        }
                        else{
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>BDEngine Animation Conversion Failed! <gray>("+anim.getAnimationTag()+")"));
                            player.playSound(player, Sound.ENTITY_SHULKER_AMBIENT, 1, 1.5f);
                        }
                    }, 1);
                    cancel();
                    return;
                }

                //Apply Transformations, Texture Values, etc.
                ZipEntry entry = frames.get(i);
                List<String> commands = executeCommands(entry, zipFile, player, createdGroup.getLocation());

                //Create Frame
                SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 2).setTransformation(createdGroup);
                frame.setStartCommands(commands);
                anim.addFrame(frame);
                i++;
            }
        }.runTaskTimer(DisplayAPI.getPlugin(), 0, 2); //BDEngine Animation Frame Duration is 2 ticks
    }


    private List<String> executeCommands(ZipEntry zipEntry, ZipFile zipFile, Player player, Location location){
        List<String> commands;
        if (zipEntry.getName().endsWith(createModelPath)) {
            commands = null;
        }
        else{
            commands = new ArrayList<>();
        }
        try{
            InputStream stream = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = br.readLine()) != null){
                if (line.startsWith("#") || line.isEmpty()){
                    continue;
                }

                if (zipEntry.getName().endsWith("/check_loop.mcfunction") || zipEntry.getName().contains("/check_pause_")){
                    continue;
                }

                //Summon Model from datapack
                if (zipEntry.getName().endsWith(createModelPath)){

                    //Master Part
                    //if (line.contains("execute as @s")){
                    if (line.startsWith("summon block_display")){
                        String coordinates = ConversionUtils.getCoordinateString(location);
                        String replacement = "execute at "+player.getName()+" run summon block_display "+coordinates;
                        line = line.replace("summon block_display ~ ~ ~", replacement); //Before BDEngine 1.15.3
                        line = line.replace("summon block_display ~-0.5 ~-0.5 ~-0.5", replacement); //BDEngine 1.15.3 and Later
                        try{
                            DatapackEntitySpawned.prepareAnimationMaster(projectName);
                        }
                        catch(NumberFormatException e){
                            br.close();
                            zipFile.close();
                            player.sendMessage(Component.text("Animation conversion failed! Read console", NamedTextColor.RED));
                            throw new RuntimeException("Failed to read command from zip entry: Invalid timestamp value");
                        }
                        catch(ArrayIndexOutOfBoundsException e){
                            br.close();
                            zipFile.close();
                            player.sendMessage(Component.text("Animation conversion failed! Read console", NamedTextColor.RED));
                            throw new RuntimeException("Failed to read command from zip entry: Wrong game version downloaded");
                        }
                    }

                    //Sub-Master Parts
                    else if (line.contains("@s")){
                        line = line.replace("@s", player.getName());
                        line = "execute"+line.split("nearest]")[1];
                    }
                    line = line.substring(0, line.length()-2)+",\""+LocalManager.datapackConvertDeleteSubParentTag+"\"]}";
                }

                else if (line.startsWith("schedule")) {
                    continue;
                }

                String coordinates = ConversionUtils.getCoordinateString(location);
                World w = location.getWorld();
                String worldName = ConversionUtils.getExecuteCommandWorldName(w);


                if (!line.startsWith("data merge entity @e[") && commands != null){ //Not an animation command
                    commands.add(line);
                }
                Bukkit.dispatchCommand(silentSender, "execute positioned "+coordinates+" in "+worldName+" run "+line);
            }
            br.close();
        }
        catch (IOException e){
            player.sendMessage(Component.text("Animation conversion failed! Read console"));
            try{
                zipFile.close();
            }
            catch(IOException ignored){}
            throw new RuntimeException("Failed to execute command from ZipEntry: "+zipEntry.getName());
        }
        return commands;
    }
}
