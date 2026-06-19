package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.Direction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class DEUSubCommand {
    private final Permission permission;
    protected final TreeMap<Integer, TabSuggestion> tabCompleteSuggestions = new TreeMap<>();
    private int minimumArgs;
    protected final Set<String> flags = new TreeSet<>();
    protected final TreeMap<String, List<String>> options = new TreeMap<>();
    protected final TreeMap<String, DEUSubCommand> subCommands = new TreeMap<>();
    private final String shortUsage;
    protected String usage;
    private boolean unsafe;


    DEUSubCommand(@NotNull String commandName, @NotNull Permission permission){
        this.permission = permission;
        shortUsage = "/deu "+commandName;
        usage = shortUsage;
    }

    DEUSubCommand(String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission){
        this.permission = permission;
        parentSubCommand.subCommands.put(commandName, this);
        shortUsage = parentSubCommand.usage +
                " " +
                commandName;
    }

    protected void setUnsafe(){
        this.unsafe = true;
    }

    public boolean isUnsafe(){
        return this.unsafe;
    }

    protected boolean hasMinimumArguments(CommandSender sender, String[] args){
        if (tabCompleteSuggestions.isEmpty()) return true;
        if (args.length < minimumArgs){
            incorrectUsage(sender);
            return false;
        }
        return true;
    }

    protected void incorrectUsage(CommandSender sender){
        sender.sendMessage(DisplayAPI.pluginPrefix
                .append(Component.text("Incorrect Usage! "+getCommandUsage(), NamedTextColor.RED)));
    }


    protected DEUSubCommand getCommand(@NotNull String command){
        return subCommands.get(command);
    }

    protected String getShortCommandUsage(){
        return shortUsage;
    }

    protected String getCommandUsage(){
        if (usage == null) buildUsage();
        return usage;
    }

    protected abstract String getDescription();

    private void buildUsage(){
        StringBuilder sb = new StringBuilder(shortUsage);

        //Tab Completions
        if (!tabCompleteSuggestions.isEmpty()){
            for (TabSuggestion entry : tabCompleteSuggestions.sequencedValues()){
                List<String> suggestions = entry.displayedSuggestions;

                sb.append(" ");
                if (suggestions.size() > 1) sb.append("<");
                sb.append(String.join(" | ", suggestions));
                if (suggestions.size() > 1) sb.append(">");
            }
        }

        //Flags
        for (String flag : flags){
            sb.append(" [").append(flag).append("]");
        }

        //Options
        for (Map.Entry<String, List<String>> entry : options.entrySet()){
            String option = entry.getKey();
            List<String> values = entry.getValue();

            sb.append(" ");
            sb.append("[").append(option);
            if (!values.isEmpty()) {
                sb.append(" ");
                if (values.size() > 1) sb.append("<");
                sb.append(String.join(" | ", values));
                if (values.size() > 1) sb.append(">");
            }
            sb.append("]");
        }

        usage = sb.toString();
    }

    protected TabSuggestion setTabComplete(int index, String suggestion){
        TabSuggestion s = new TabSuggestion(List.of(suggestion));
        setTabComplete(index, s, true);
        return s;
    }

    protected TabSuggestion setOptionalTabComplete(int index, String suggestion){
        TabSuggestion s = new TabSuggestion(List.of(suggestion));
        setTabComplete(index, s, false);
        return s;
    }

    protected TabSuggestion setTabComplete(int index, List<String> suggestions){
        TabSuggestion suggestion = new TabSuggestion(suggestions);
        setTabComplete(index, suggestion, true);
        return suggestion;
    }

    protected void setTabComplete(int index, TabSuggestion suggestion){
        setTabComplete(index, suggestion, true);
    }

    private void setTabComplete(int index, TabSuggestion tabSuggestion, boolean updateMinimum){
        tabCompleteSuggestions.put(index, tabSuggestion);
        if (updateMinimum) minimumArgs = index+1;
    }

    protected void addFlag(@NotNull String flag){
        flags.add(flag.toLowerCase());
    }

    protected void addOption(@NotNull String option, @NotNull String inputPlaceholder){
        addOption(option.toLowerCase(), List.of(inputPlaceholder));
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
            String arg = args[i].toLowerCase();

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
            return flags.contains(flag.toLowerCase());
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
        public static final TabSuggestion X_COORDINATE = coordinate('x');
        public static final TabSuggestion Y_COORDINATE = coordinate('y');
        public static final TabSuggestion Z_COORDINATE = coordinate('z');


        public boolean suggestUsingCurrentString = false;
        List<String> suggestions;
        List<String> displayedSuggestions;

        private static final String COORDINATE_SUFFIX = "_coord";

        TabSuggestion(List<String> suggestions){
            this.suggestions = suggestions;
            this.displayedSuggestions = suggestions;
        }

        private static TabSuggestion coordinate(char coord){
            List<String> list = new ArrayList<>();
            list.add(coord+COORDINATE_SUFFIX);
            list.add("<"+coord+">");
            return new TabSuggestion(list)
                    .suggestUsingCurrentString()
                    .setDisplayedSuggestions(List.of("<"+coord+">"));
        }

        public TabSuggestion setDisplayedSuggestions(List<String> usageSuggestions){
            this.displayedSuggestions = usageSuggestions;
            return this;
        }

        public TabSuggestion suggestUsingCurrentString(){
            this.suggestUsingCurrentString = true;
            return this;
        }

        public List<String> getTabComplete(String current, CommandSender sender){
            if (suggestions == null) return List.of();

            if (!suggestUsingCurrentString){
                return suggestions;
            }

            List<String> list = new ArrayList<>();
            for (String s : suggestions){
                //Autofill coordinate
                if (s.endsWith(COORDINATE_SUFFIX) && current.isEmpty()){
                    if (!(sender instanceof Player player)) continue;
                    switch (s.charAt(0)){
                        case 'x' -> {
                            list.add(String.format("%.2f", player.getX()));
                        }
                        case 'y' -> {
                            list.add(String.format("%.2f", player.getY()));
                        }
                        case 'z' -> {
                            list.add(String.format("%.2f", player.getZ()));
                        }
                    }
                }
                else if (s.toLowerCase().startsWith(current.toLowerCase())){
                    list.add(s);
                }
            }
            return list.isEmpty() ? suggestions : list;
        }
    }
}
