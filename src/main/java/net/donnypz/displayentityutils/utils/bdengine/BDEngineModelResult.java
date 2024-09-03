package net.donnypz.displayentityutils.utils.bdengine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

/**
 * The result of a BDEngine Request after using {@link BDEngineUtils#requestModel(int)}
 */
public final class BDEngineModelResult {
    private static final CommandSender silentSender = Bukkit.createCommandSender(feedback -> {});
    private String name;
    private String author;
    private String category;
    private String img_url;
    private int model_count;
    private LinkedHashMap<Integer, String> commands;
    //ArrayList<BDEnginePassenger> Passengers; //For Saving a BDEngine model directly to a storage location

    BDEngineModelResult(){}


    /**
     * Spawn the model stored within the result at a location.
     * This method should be run synchronously.
     * @param location the location to spawn the model
     * @return false if the location's chunk is not loaded
     */
    public boolean spawn(@NotNull Location location){
        if (!location.isChunkLoaded()){
            return false;
        }
        for (String command : commands.sequencedValues()){
            String coordinates = location.x()+" "+location.y()+" "+location.z();
            String replacement = "summon block_display "+coordinates;
            String newCommand = command.replace("summon block_display ~-0.5 ~-0.5 ~-0.5", replacement);
            Bukkit.dispatchCommand(silentSender, newCommand);
        }
        return true;
    }

    /**
     * Get the number of commands required to spawn the entire model
     * @return an int
     */
    public int getCommandCount() {
        return commands.size();
    }

    /**
     * Get the name of this model
     * @return a string
     */
    public String getName(){
        return name;
    }

    /**
     * Get the name of the author who created this model
     * @return a string
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the name of this model's category
     * @return a string
     */
    public String getCategory(){
        return category;
    }

    /**
     * Get the total number of elements in this model
     * @return an int
     */
    public int getModelCount(){
        return model_count;
    }
}
