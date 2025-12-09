package net.donnypz.displayentityutils.utils.bdengine.convert.datapack;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.listeners.bdengine.DatapackEntitySpawned;
import net.donnypz.displayentityutils.managers.*;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@ApiStatus.Internal
public class BDEngineLegacyDPConverter {

    @ApiStatus.Internal
    public static void saveDatapackAnimation(@NotNull Player player, @NotNull String datapackName, @NotNull String groupSaveTag, @NotNull String animationSaveTag){
        if (!datapackName.endsWith(".zip")){
            datapackName = datapackName+".zip";
        }

        player.sendMessage(Component.text("Legacy Conversion - If no model/group/result is present, the datapack is likely a modern one", NamedTextColor.LIGHT_PURPLE));
        try{
            ZipFile zipFile = new ZipFile(PluginFolders.animDatapackFolder+"/"+datapackName);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            List<ZipEntry> frames = new ArrayList<>();

            long timestamp = 0;
            int totalGroups = 0;
            Location pLoc = player.getLocation();
            while (entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()){
                    continue;
                }
                if (entry.getName().contains("animation_keyframe")){
                    frames.add(entry);
                    continue;
                }
                if (entry.getName().contains("summon.mcfunction")){
                    timestamp = executeCommands(entry, zipFile, player, pLoc, true);
                }
                if (entry.getName().contains("start_animation.mcfunction")){
                    totalGroups = (int) executeCommands(entry, zipFile, player, pLoc, true);
                }
            }
            String finalDatapackName = datapackName;
            if (!frames.isEmpty() && timestamp > 0 && totalGroups > 0){
                int finalTotalGroups = totalGroups;
                long finalTimestamp = timestamp;
                DisplayAPI.getScheduler().runLater(() -> {
                    readAnimationFiles(finalTimestamp, finalTotalGroups, zipFile, frames, finalDatapackName, player, groupSaveTag, animationSaveTag);
                }, 30);
            }
        } catch (IOException e) {
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("Failed to find datapack with the provided name! Ensure that the datapack is placed in the \"bdenginedatapacks\" folder of this plugin, as a .zip file", NamedTextColor.RED)));
        }
    }

    private static void readAnimationFiles(long timeStamp, int totalGroups, ZipFile zipFile, List<ZipEntry> frames, String datapackName, @NotNull Player player, @NotNull String groupSaveTag, @NotNull String animationSaveTag){
        SpawnedDisplayEntityGroup createdGroup = DatapackEntitySpawned.getTimestampGroup(timeStamp);
        if (createdGroup == null){
            player.sendMessage(Component.text("Failed to find model/group created from datapack!", NamedTextColor.RED));
            player.sendMessage(Component.text("| The datapack may be a modern one (v1.13+ of BDEngine). Try using /deu bdengine convertdp"));
            return;
        }

        DatapackEntitySpawned.finalizeAnimationPreparation(timeStamp);
        createdGroup.seedPartUUIDs(SpawnedDisplayEntityGroup.defaultPartUUIDSeed);
        final SpawnedDisplayAnimation anim = new SpawnedDisplayAnimation();


        DisplayAPI.getScheduler().partRunTimer(createdGroup.getMasterPart(), new Scheduler.SchedulerRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (i == frames.size()){
                    try{
                        createdGroup.setToFrame(anim, anim.getFrames().getFirst());
                    }
                    catch(IndexOutOfBoundsException ignored){}


                    //Save with first frame applied to group
                    DisplayAPI.getScheduler().runLater(() -> {
                        if (animationSaveTag.isBlank()){
                            anim.setAnimationTag(datapackName.replace(".zip", "_autoconvert"));
                        }
                        else{
                            anim.setAnimationTag(animationSaveTag);
                        }
                        boolean animationSuccess = DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim.toDisplayAnimation(), player);
                        anim.remove();

                        boolean groupSuccess;
                        player.sendMessage(Component.empty());
                        if (!groupSaveTag.equals("-")){
                            if (groupSaveTag.isBlank()){
                                createdGroup.setTag(datapackName.replace(".zip", "_autoconvert"));
                            }
                            else{
                                createdGroup.setTag(groupSaveTag);
                            }
                            groupSuccess = DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.LOCAL, createdGroup.toDisplayEntityGroup(), player);
                        }
                        else{
                            groupSuccess = false;
                        }

                        createdGroup.unregister(true, true);

                        if (animationSuccess){
                            player.sendMessage(DisplayAPI.pluginPrefix
                                    .append(Component.text("Successfully converted BDEngine animation to DisplayAnimation.", NamedTextColor.GREEN)));
                            player.sendMessage(Component.text(" | Animation Tag: "+anim.getAnimationTag(), NamedTextColor.GRAY));
                            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
                        }


                        if (groupSuccess){
                            player.sendMessage(Component.text(" | Group Tag: "+createdGroup.getTag(), NamedTextColor.GRAY));
                        }
                        else{
                            if (groupSaveTag.equals("-")){
                                player.sendMessage(Component.text("The group was not created during this conversion due to setting the group tag to \"-\"", NamedTextColor.GRAY));
                            }
                            else{
                                player.sendMessage(Component.text("An error occured when saving the group created from BDEngine conversion, refer to console and report errors if necessary", NamedTextColor.RED));
                            }
                        }
                        player.sendMessage(Component.empty());

                        try{
                            zipFile.close();
                        }
                        catch(IOException e){
                            throw new RuntimeException("Failed to close zip file");
                        }
                    },1);
                    cancel();
                    return;
                }



                //Plays the command for the next frame for every group in the animation
                //If 2 groups exist, it will execute animation_keyframe0_1.mcfunction and animation_keyframe0_2.mcfunction
                //then animation_keyframe1_1.mcfunction and animation_keyframe1_2.mcfunction on the next iteration, and so on...
                Location pLoc = player.getLocation();
                for (int j = 0; j < totalGroups; j++){
                    ZipEntry entry = frames.get(i);
                    executeCommands(entry, zipFile, player, pLoc, false);
                    i++;
                }

                //Create Frame
                SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 2).setTransformation(createdGroup);
                if (!frame.isEmptyFrame()){
                    anim.addFrame(frame);
                }
            }
        }, 2, 2);
    }


    private static long executeCommands(ZipEntry zipEntry, ZipFile zipFile, Player player, Location location, boolean expectData){
        try{
            InputStream stream = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            long value = 0;
            while ((line = br.readLine()) != null){
                if (line.startsWith("#") || line.isEmpty()){
                    continue;
                }

                if (zipEntry.getName().contains("summon")){
                    String projectName = zipEntry.getName();
                    projectName = projectName.replace(BDEngineDPConverter.FUNCTION_FOLDER +"/summon.mcfunction", "");

                    String[] splitDir = projectName.split("/");
                    projectName = splitDir[splitDir.length-1];

                    //Master Part
                    if (line.contains("execute as @s")){

                        String coordinates = ConversionUtils.getCoordinateString(location);
                        String replacement = "execute at "+player.getName()+" run summon block_display "+coordinates;
                        line = line.replace("execute as @s run summon block_display ~ ~ ~", replacement);
                        String[] timestampSplit = line.split(":\\[\""+projectName.replace("_", ""));
                        try{
                            value = Long.parseLong(timestampSplit[1].replace("\"]}", ""));
                            DatapackEntitySpawned.prepareAnimationMaster(value);
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
                        //String coordinates = location.x()+" "+location.y()+" "+location.z();
                        //String replacement = "positioned "+coordinates;
                        String replacement = player.getName();
                        line = line.replace("@s", replacement);
                    }
                    line = line.substring(0, line.length()-2)+",\""+LocalManager.datapackConvertDeleteSubParentTag+"\"]}";
                }

                else if (zipEntry.getName().contains("start_animation.mcfunction")){
                    if (line.contains("function")){
                        value++;
                        continue;
                    }
                }
                else if (line.contains("function")) {
                    continue;
                }

                Bukkit.dispatchCommand(BDEngineDPConverter.silentSender, line);

            }
            br.close();
            stream.close();

            //True if command was summon.mcfuntion command, returns group timestamp created by BDEngine
            //Also to get total number of groups created by BDEngine commands
            if (expectData){
                return value;
            }
            else{
                return 0;
            }
        }
        catch (IOException e){
            player.sendMessage(Component.text("Animation conversion failed! Read console"));
            throw new RuntimeException("Failed to execute command from ZipEntry: "+zipEntry.getName());
        }
    }
}
