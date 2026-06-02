package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.Direction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Pose;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class DEUSubCommand {
    private final Permission permission;
    protected final TreeMap<Integer, TabSuggestion> tabCompleteSuggestions = new TreeMap<>();
    protected final Set<String> flags = new HashSet<>();
    protected final HashMap<String, List<String>> options = new HashMap<>();
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

    protected void addFlag(@NotNull String flag){
        flags.add(flag);
    }

    protected void addOption(@NotNull String option, @NotNull String inputPlaceholder){
        addOption(option, List.of(inputPlaceholder));
    }

    protected void addOption(@NotNull String option, @NotNull List<String> inputPlaceholders){
        options.put(option, new ArrayList<>(inputPlaceholders));
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isHelpCommand(){
        return permission == Permission.HELP;
    }

    protected @NotNull OptionalArguments getOptionalArguments(CommandSender sender, String[] args){
        int startIndex = tabCompleteSuggestions.isEmpty()
                ? 0
                : tabCompleteSuggestions.sequencedKeySet().getLast() + 1;
        return getOptionalArguments(sender, args, startIndex);
    }

    protected @NotNull OptionalArguments getOptionalArguments(CommandSender sender, String[] args, int startIndex){
        OptionalArguments oArgs = new OptionalArguments();
        for (int i = startIndex; i < args.length; i++) {
            String arg = args[i];

            if (flags.contains(arg)){
                oArgs.flags.add(arg);
                continue;
            }
            if (options.containsKey(arg)){
                if (i + 1 >= args.length) {
                    List<String> placeholders = options.get(arg);
                    String expected = placeholders.size() == 1 ? placeholders.getFirst() : "<"+String.join(" | ", placeholders)+">";
                    sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text(
                            String.format("Incorrect Usage! \"%s\" expects %s.", arg, expected),
                            NamedTextColor.RED)));
                    oArgs.isValidOptions = false;
                    return oArgs;
                }
                oArgs.options.put(arg, args[++i]);
            }
        }

        return oArgs;
    }

    protected static class OptionalArguments{
        Set<String> flags = new HashSet<>();
        Map<String, String> options = new HashMap<>();
        boolean isValidOptions = true;

        private OptionalArguments(){}

        public boolean hasFlag(@NotNull String flag){
            return flags.contains(flag);
        }

        public @NotNull String getOption(@NotNull String option){
            return option.startsWith("-") ? options.getOrDefault(option, "") : options.getOrDefault("-"+option, "");
        }

        public boolean isValidOptions() {
            return isValidOptions;
        }
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
