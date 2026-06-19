package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCMD extends ConsoleUsableSubCommand {
    Component incorrectUsageMessage;
    int minLength;
    private final int STORAGE_INDEX = 2;
    private final int PAGE_NUMBER_INDEX = 3;
    boolean listsGroups;

    public ListCMD(Component incorrectUsageMessage, int minLength, boolean listsGroups) {
        super(
                listsGroups ? "group list" : "anim list",
                listsGroups ? Permission.LIST_GROUPS : Permission.LIST_ANIMATIONS
        );
        super.usage = null;
        this.incorrectUsageMessage = incorrectUsageMessage;
        this.minLength = minLength;
        this.listsGroups = listsGroups;
        setTabComplete(STORAGE_INDEX, TabSuggestion.STORAGES);
        setOptionalTabComplete(PAGE_NUMBER_INDEX, "[page-number]");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasMinimumArguments(sender, args)) return;
        list(sender, args);
    }

    public void list(CommandSender sender, String[] args) {
        DisplayAPI.getScheduler().runAsync(() -> {
            LoadMethod loadMethod;
            try {
                loadMethod = LoadMethod.valueOf(args[STORAGE_INDEX].toUpperCase());
            } catch (IllegalArgumentException e) {
                DisplayEntityPluginCommand.invalidStorage(sender);
                return;
            }

            List<String> tags = listsGroups ?
                    DisplayGroupManager.getSavedDisplayEntityGroups(loadMethod)
                    :
                    DisplayAnimationManager.getSavedDisplayAnimations(loadMethod);

            sender.sendMessage(DisplayAPI.pluginPrefixLong);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("Storage Location: <yellow>" + loadMethod.getDisplayName()));
            if (tags.isEmpty()) {
                sender.sendMessage(Component.text("That storage location is empty!", NamedTextColor.RED));
                return;
            }

            if (sender instanceof Player) {
                sender.sendMessage(Component.text("Click to a listed item to " + (listsGroups ? "spawn" : "select") + " it", NamedTextColor.GRAY));
            }

            int pageNumber = 1;
            if (args.length > minLength) {
                try {
                    pageNumber = Math.max(1, Integer.parseInt(args[PAGE_NUMBER_INDEX]));
                } catch (NumberFormatException ignored) {
                }
            }

            int end = pageNumber * 6;
            int start = end - 6;

            for (int i = start; i < end; i++) {
                if (i >= tags.size()) {
                    break;
                }
                String tag = tags.get(i);
                sender.sendMessage(getMessage(tag, loadMethod));
            }
            sender.sendMessage(Component.text("----------" + "Page " + pageNumber + "----------", NamedTextColor.GRAY));
        });
    }


    private Component getMessage(String tag, LoadMethod loadMethod) {
        return MiniMessage.miniMessage().deserialize("- <yellow>" + tag)
                .hoverEvent(HoverEvent.showText(
                        Component.text(
                                (listsGroups ? "Click to spawn" : "Click to select"),
                                NamedTextColor.GREEN))
                ).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/deu " + (listsGroups ? "group spawn " : "anim select ") + tag + " " + loadMethod.name()));
    }

    @Override
    protected String getDescription() {
        return listsGroups ? "List all saved display entity groups/models"
                : "List all saved animations";
    }
}
