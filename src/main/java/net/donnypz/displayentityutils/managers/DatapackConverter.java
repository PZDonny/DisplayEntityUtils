package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.listeners.bdengine.DEUEntitySpawned;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class DatapackConverter {

    private static final String createModelPath = "/create.mcfunction";
    private String projectName = null;
    private final LinkedHashMap<String, ArrayList<ZipEntry>> animations = new LinkedHashMap<>();

    DatapackConverter(Player player, String datapackName, String groupSaveTag, String animationSaveTag){
        try{
            ZipFile zipFile = new ZipFile(LocalManager.getAnimationDatapackFolder()+"/"+datapackName);
            searchEntries(player, datapackName, zipFile.entries(), zipFile, groupSaveTag, animationSaveTag);
        }
        catch (IOException e) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix
                    .append(Component.text("Failed to find datapack with the provided name! Ensure that the datapack is placed in the \"bdenginedatapacks\" folder of this plugin, as a .zip file", NamedTextColor.RED)));
        }
    }

    private String getAnimationName(String entryName, String folder){
        String animName;
        if (VersionUtils.is1_21){
            animName = entryName.split("function/"+folder+"/")[1];
        }
        else{
            animName = entryName.split("functions/"+folder+"/")[1];
        }
        if (animName.endsWith(".mcfunction")){
            animName = animName.split("/")[0];
        }
        else{
            animName = animName.substring(0, animName.length()-1);
        }
        return animName;
    }

    private String getProjectName(String entryName){
        if (projectName == null){
            String projectName;

            if (VersionUtils.is1_21){
                projectName = entryName.split("/function/")[0];
            }
            else{
                projectName = entryName.split("/functions/")[0];
            }
            projectName = projectName.substring(5);
            this.projectName = projectName;
        }
        return projectName;
    }

    private void searchEntries(Player player, String datapackName, Enumeration<? extends ZipEntry> entries, ZipFile zipFile, String groupSaveTag, String animationSaveTagPrefix){
        Location pLoc = player.getLocation();


        String entryName;
        ZipEntry createEntry = null;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            entryName = entry.getName();
            if (!entryName.endsWith(".mcfunction") &&
                    ((VersionUtils.is1_21 && entryName.contains("/function/a/") && !entryName.endsWith("/function/a/")) || ((entryName.contains("/functions/a/") && !entryName.endsWith("/functions/a/"))))){
                animations.putIfAbsent(getAnimationName(entryName, "a"), new ArrayList<>());
            }
            else if (entryName.endsWith(createModelPath)) { //Summon Model for animation
                createEntry = entry;
            }
            else if (entryName.contains("/keyframe_") && entryName.endsWith(".mcfunction")){
                String animName = getAnimationName(entryName, "k");
                ArrayList<ZipEntry> list = animations.getOrDefault(animName, new ArrayList<>());
                list.add(entry);
                animations.put(animName, list);
            }
        }

        if (createEntry != null){
            executeCommands(createEntry, zipFile, player, pLoc);
        }



    //Save Animations
        Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () ->{
            SpawnedDisplayEntityGroup createdGroup = DEUEntitySpawned.getProjectGroup(projectName);

            if (createdGroup == null){
                player.sendMessage(Component.text("Failed to find model/group created from datapack!", NamedTextColor.RED));
                player.sendMessage(Component.text("| The datapack may be a legacy one (before v1.13 of BDEngine). Try using /mdis bdengine convertanimleg"));
                return;
            }

            DEUEntitySpawned.finalizeAnimationPreparation(projectName);
            createdGroup.seedPartUUIDs(SpawnedDisplayEntityGroup.defaultPartUUIDSeed);

            player.sendMessage(Component.empty());
            if (!groupSaveTag.equals("-")){
                if (groupSaveTag.isBlank()){
                    createdGroup.setTag(datapackName.replace(".zip", "_auto"));
                }
                else{
                    createdGroup.setTag(groupSaveTag);
                }
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.LOCAL, createdGroup.toDisplayEntityGroup(), player);
                player.sendMessage(Component.text(" | Group Tag: "+createdGroup.getTag(), NamedTextColor.GRAY));
            }
            else{
                player.sendMessage(Component.text("The group was not saved during this conversion due to setting the group tag to \"-\"", NamedTextColor.GRAY));
            }
            player.sendMessage(Component.empty());

            int delay = 0;
            for (String animName : animations.sequencedKeySet()){
                List<ZipEntry> frames = animations.get(animName);
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Converting Animations: <yellow>"+animName));
                    processAnimation(createdGroup, zipFile, frames, datapackName, animName, player, animationSaveTagPrefix);
                    }, delay);

                delay+=(frames.size()*2);
            }

            //Despawn group after all animation conversions
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                createdGroup.unregister(true, true);
                try{
                    zipFile.close();
                }
                catch(IOException e){
                    throw new RuntimeException("Failed to close zip file");
                }
            }, delay+5);
        }, 30);
    }

    private void processAnimation(SpawnedDisplayEntityGroup createdGroup, ZipFile zipFile, List<ZipEntry> frames, String datapackName, String animName, @NotNull Player player, @NotNull String animationSaveTagPrefix){

        final SpawnedDisplayAnimation anim = new SpawnedDisplayAnimation();
        SpawnedDisplayAnimationFrame initialFrame = new SpawnedDisplayAnimationFrame(-1, -1).setTransformation(createdGroup);

        final int frameCount = frames.size();
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if (i == frameCount){
                    SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 2).setTransformation(createdGroup);
                    if (!frame.isEmptyFrame()){
                        anim.addFrame(frame);
                    }

                    createdGroup.setToFrame(anim, initialFrame, false);

                    //Save with first frame applied to group
                    Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                        if (animationSaveTagPrefix.isBlank()){
                            anim.setAnimationTag(datapackName.replace(".zip", "_auto_"+animName));
                        }
                        else{
                            anim.setAnimationTag(animationSaveTagPrefix+"_"+animName);
                        }
                        boolean animationSuccess = DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim.toDisplayAnimation(), null);
                        anim.remove();



                        if (animationSuccess){
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>BDEngine Animation Conversion Successful! <gray>("+anim.getAnimationTag()+")"));
                            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
                        }
                        else{
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>BDEngine Animation Conversion Failed! <gray>("+anim.getAnimationTag()+")"));
                            player.playSound(player, Sound.ENTITY_SHULKER_AMBIENT, 1, 1.5f);
                        }
                    },1);
                    cancel();
                    return;
                }

                //Apply Transformations, Texture Values, etc.
                ZipEntry entry = frames.get(i);
                executeCommands(entry, zipFile, player, createdGroup.getLocation());

                //Create Frame
                SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 2).setTransformation(createdGroup);
                anim.addFrame(frame);
                i++;
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 2);
    }


    private void executeCommands(ZipEntry zipEntry, ZipFile zipFile, Player player, Location location){
        try{
            InputStream stream = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;


            while ((line = br.readLine()) != null){
                if (line.startsWith("#") || line.isEmpty()){
                    continue;
                }

                //Summon Model from datapack
                if (zipEntry.getName().endsWith(createModelPath)){
                    String projectName = getProjectName(zipEntry.getName());

                    //Master Part
                    //if (line.contains("execute as @s")){
                    if (line.startsWith("summon block_display")){
                        String coordinates = getCoordinateString(location);
                        String replacement = "execute at "+player.getName()+" run summon block_display "+coordinates;
                        line = line.replace("summon block_display ~ ~ ~", replacement);
                        try{
                            DEUEntitySpawned.prepareAnimationMaster(projectName);
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

                else if (line.startsWith("schedule") || line.startsWith("function")) {
                    continue;
                }
                String coordinates = getCoordinateString(location);
                Bukkit.dispatchCommand(LocalManager.silentSender, "execute positioned "+coordinates+" in "+location.getWorld().getName()+" run "+line);
            }
            br.close();
            stream.close();
        }
        catch (IOException e){
            player.sendMessage(Component.text("Animation conversion failed! Read console"));
            throw new RuntimeException("Failed to execute command from ZipEntry: "+zipEntry.getName());
        }
    }

    static String getCoordinateString(Location location){
        return location.x()+" "+location.y()+" "+location.z();
    }
}
