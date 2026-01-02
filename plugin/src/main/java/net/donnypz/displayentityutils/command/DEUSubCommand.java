package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Pose;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DEUSubCommand {
    private final Permission permission;
    protected final Map<Integer, TabSuggestion> tabCompleteSuggestions = new HashMap<>();
    protected final HashMap<String, DEUSubCommand> subCommands = new HashMap<>();

    DEUSubCommand(@NotNull Permission permission){
        this.permission = permission;
    }

    DEUSubCommand(@NotNull Permission permission, @NotNull DEUSubCommand helpSubCommand){
        this.permission = permission;
        subCommands.put("help", helpSubCommand);
    }

    DEUSubCommand(String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission){
        this.permission = permission;
        parentSubCommand.subCommands.put(commandName, this);
    }

    protected TabSuggestion setTabComplete(int index, String suggestion){
        return setTabComplete(index, List.of(suggestion));
    }

    protected TabSuggestion setTabComplete(int index, List<String> suggestions){
        TabSuggestion suggestion = new TabSuggestion(suggestions);
        tabCompleteSuggestions.put(index, new TabSuggestion(suggestions));
        return suggestion;
    }

    protected void setTabComplete(int index, TabSuggestion suggestion){
        tabCompleteSuggestions.put(index, suggestion);
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isHelpCommand(){
        return permission == Permission.HELP;
    }

    protected static class TabSuggestion{
        public static final TabSuggestion STORAGES = new TabSuggestion(List.of("local", "mysql", "mongodb"))
                .suggestUsingCurrentString();
        public static final TabSuggestion BILLBOARDS = new TabSuggestion(Arrays.stream(Display.Billboard.values()).map(Enum::name).toList())
                .suggestUsingCurrentString();
        public static final TabSuggestion PART_TYPES = new TabSuggestion(List.of("block", "item", "text", "interaction", "mannequin"))
                .suggestUsingCurrentString();
        public static final TabSuggestion MANNEQUIN_POSES = new TabSuggestion(List.of(
                Pose.SLEEPING.name(),
                Pose.SWIMMING.name(),
                Pose.SNEAKING.name(),
                Pose.STANDING.name(),
                Pose.FALL_FLYING.name() //ELYTRA
                ))
                .suggestUsingCurrentString();
        public static final TabSuggestion ITEM_DISPLAY_TRANSFORMS = new TabSuggestion(Arrays.stream(ItemDisplay.ItemDisplayTransform.values()).map(Enum::name).toList())
                .suggestUsingCurrentString();
        public static final TabSuggestion TEXT_DISPLAY_ALIGN = new TabSuggestion(Arrays.stream(TextDisplay.TextAlignment.values()).map(Enum::name).toList())
                .suggestUsingCurrentString();
        public static final TabSuggestion TEXT_DISPLAY_FONTS = new TabSuggestion(List.of("default", "uniform", "alt", "illageralt"))
                .suggestUsingCurrentString();
        public static final TabSuggestion DIRECTIONS = new TabSuggestion(Arrays.stream(Direction.values()).map(Enum::name).toList())
                .suggestUsingCurrentString();
        public static final TabSuggestion COLORS = new TabSuggestion(List.of(
                "<hex-color>",
                "white",
                "silver",
                "gray",
                "black",
                "red",
                "maroon",
                "olive",
                "lime",
                "green",
                "aqua",
                "teal",
                "blue",
                "navy",
                "fuchsia",
                "purple",
                "orange"))
                .suggestUsingCurrentString();


        public boolean suggestUsingCurrentString = false;
        List<String> suggestions;

        TabSuggestion(List<String> suggestions){
            this.suggestions = suggestions;
        }

        public TabSuggestion suggestUsingCurrentString(){
            this.suggestUsingCurrentString = true;
            return this;
        }
    }
}
