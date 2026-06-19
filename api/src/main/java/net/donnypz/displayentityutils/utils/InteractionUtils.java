package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class InteractionUtils {

    public static final NamespacedKey leftClickConsole = new NamespacedKey(DisplayAPI.getPlugin(), "lcc");
    public static final NamespacedKey leftClickPlayer = new NamespacedKey(DisplayAPI.getPlugin(), "lcp");
    public static final NamespacedKey rightClickConsole = new NamespacedKey(DisplayAPI.getPlugin(), "rcc");
    public static final NamespacedKey rightClickPlayer = new NamespacedKey(DisplayAPI.getPlugin(), "rcp");

    private InteractionUtils(){}

    /**
     * Gets the center location of an {@link Interaction}
     * @param interaction The interaction entity get the center of
     * @return The interaction's center location
     */
    public static Location getInteractionCenter(@NotNull Interaction interaction){
        Location loc = interaction.getLocation().clone();
        double yCenter = interaction.getInteractionHeight()/2;
        loc.add(0, yCenter, 0);
        return loc;
    }

    /**
     * Adds a command to an {@link Interaction} to execute when clicked
     * @param interaction The entity to assign the command to
     * @param command The command to assign
     * @param isLeftClick whether the command is executed on left click
     * @param isConsole whether the command should be executed by console or the clicker
     */
    @ApiStatus.Internal
    public static void addInteractionCommand(@NotNull Interaction interaction, @NotNull String command, boolean isLeftClick, boolean isConsole){
        if (command.isBlank()){
            return;
        }
        NamespacedKey key;
        if (!isLeftClick){
            key = isConsole ? rightClickConsole : rightClickPlayer;
        }
        else{
            key = isConsole ? leftClickConsole : leftClickPlayer;
        }
        DisplayUtils.addToPDCList(interaction, command, key);
    }

    /**
     * Remove a command from an {@link Interaction}
     * @param interaction The entity to assign the command to
     * @param command The command to remove
     */
    public static void removeInteractionCommand(@NotNull Interaction interaction, @NotNull InteractionCommand command){
        String cmd = command.command;
        NamespacedKey key = command.key;
        DisplayUtils.removeFromPDCList(interaction, cmd, key);
    }


    /**
     * Gets the commands present on an {@link Interaction}, with the interaction command prefix included.
     * @param interaction the interaction entity
     * @return List of commands stored on this interaction entity
     */
    public static @NotNull List<String> getInteractionCommands(@NotNull Interaction interaction){
        List<String> commands = new ArrayList<>();
        commands.addAll(getInteractionLeftConsoleCommands(interaction));
        commands.addAll(getInteractionLeftPlayerCommands(interaction));
        commands.addAll(getInteractionRightConsoleCommands(interaction));
        commands.addAll(getInteractionRightPlayerCommands(interaction));
        return commands;
    }

    public static @NotNull List<String> getInteractionLeftConsoleCommands(@NotNull Interaction interaction){
        return DisplayUtils.getPDCList(interaction, leftClickConsole);
    }

    public static @NotNull List<String> getInteractionLeftPlayerCommands(@NotNull Interaction interaction){
        return DisplayUtils.getPDCList(interaction, leftClickPlayer);
    }

    public static @NotNull List<String> getInteractionRightConsoleCommands(@NotNull Interaction interaction){
        return DisplayUtils.getPDCList(interaction, rightClickConsole);
    }

    public static @NotNull List<String> getInteractionRightPlayerCommands(@NotNull Interaction interaction){
        return DisplayUtils.getPDCList(interaction, rightClickPlayer);
    }

    /**
     * Gets the commands present on an {@link Interaction}, without the plugin's interaction command prefix.
     * @param interaction
     * @return List of commands stored on the interaction entity as {@link InteractionCommand}
     */
    public static @NotNull List<InteractionCommand> getInteractionCommandsWithData(@NotNull Interaction interaction){
        List<InteractionCommand> cmd = new ArrayList<>();
        for (String s : getInteractionLeftConsoleCommands(interaction)){
            cmd.add(new InteractionCommand(s, true, true, leftClickConsole));
        }
        for (String s : getInteractionLeftPlayerCommands(interaction)){
            cmd.add(new InteractionCommand(s, true, false, leftClickPlayer));
        }
        for (String s : getInteractionRightConsoleCommands(interaction)){
            cmd.add(new InteractionCommand(s, false, true, rightClickConsole));
        }
        for (String s : getInteractionRightPlayerCommands(interaction)){
            cmd.add(new InteractionCommand(s, false, false, rightClickPlayer));
        }
        return cmd;
    }

    /**
     * Scale an Interaction entity over a period of time
     * @param interaction the interaction entity
     * @param newHeight the height to set
     * @param newWidth the width to set
     * @param durationInTicks how long the scaling should take
     * @param delayInTicks how long before the scaling should start
     */
    public static void scaleInteraction(@NotNull Interaction interaction, float newHeight, float newWidth, int durationInTicks, int delayInTicks){
        if (durationInTicks <= 0 && delayInTicks <= 0){
            interaction.setInteractionHeight(newHeight);
            interaction.setInteractionWidth(newWidth);
            return;
        }
        float heightChange = (interaction.getInteractionHeight()-newHeight)/durationInTicks;
        float widthChange = (interaction.getInteractionWidth()-newWidth)/durationInTicks;
        DisplayAPI.getScheduler().entityRunTimer(interaction, new Scheduler.SchedulerRunnable() {
            int timeRan = 0;
            @Override
            public void run() {
                if (timeRan == durationInTicks){
                    interaction.setInteractionHeight(newHeight);
                    interaction.setInteractionWidth(newWidth);
                    cancel();
                    return;
                }
                interaction.setInteractionWidth(interaction.getInteractionWidth()-widthChange);
                interaction.setInteractionHeight(interaction.getInteractionHeight()-heightChange);
                timeRan++;
            }
        }, delayInTicks, 1);
    }
}
