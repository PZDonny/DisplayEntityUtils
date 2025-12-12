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

import java.util.List;

public class ListCMD extends ConsoleUsableSubCommand {
    Component incorrectUsageMessage;
    int minLength, storageIndex, pageNumberIndex;
    boolean listsGroups;
    public ListCMD(Component incorrectUsageMessage, int minLength, boolean listsGroups) {
        super(Permission.LIST_GROUPS);
        this.incorrectUsageMessage = incorrectUsageMessage;
        this.minLength = minLength;
        this.storageIndex = minLength-1;
        this.pageNumberIndex = minLength;
        this.listsGroups = listsGroups;
        setTabComplete(storageIndex, TabSuggestion.STORAGES);
        setTabComplete(pageNumberIndex, "[page-number]");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < minLength){
            sender.sendMessage(incorrectUsageMessage);
            return;
        }
        list(sender,storageIndex, pageNumberIndex, args);
    }

    public void list(CommandSender sender, int storageIndex, int pageNumberIndex, String[] args){
        DisplayAPI.getScheduler().runAsync(() -> {
            LoadMethod method;
            try{
                method = LoadMethod.valueOf(args[storageIndex].toUpperCase());
            }
            catch(IllegalArgumentException e){
                DisplayEntityPluginCommand.invalidStorage(sender);
                return;
            }

            List<String> tags = listsGroups ?
                    DisplayGroupManager.getSavedDisplayEntityGroups(method)
                    :
                    DisplayAnimationManager.getSavedDisplayAnimations(method);

            sender.sendMessage(DisplayAPI.pluginPrefixLong);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("Storage Location: <yellow>"+method.getDisplayName()));
            if (tags.isEmpty()){
                sender.sendMessage(Component.text("That storage location is empty!", NamedTextColor.RED));
                return;
            }

            int pageNumber = 1;
            if (args.length > minLength){
                try{
                    pageNumber = Math.max(1, Integer.parseInt(args[pageNumberIndex]));
                }
                catch(NumberFormatException ignored){}
            }

            int end = pageNumber*7;
            int start = end-7;
            sender.sendMessage(Component.text("Page Number: "+pageNumber, NamedTextColor.AQUA));
            for (int i = start; i <= end; i++){
                if (i >= tags.size()){
                    break;
                }
                Component message;
                String tag = tags.get(i);
                if (listsGroups){
                    message = spawnGroup(tag, method);
                }
                else{
                    message = selectAnimation(tag, method);
                }
                sender.sendMessage(message);
            }
            sender.sendMessage("------------------------");
        });
    }

    private static Component spawnGroup(String tag, LoadMethod loadMethod){
        return MiniMessage.miniMessage().deserialize("- <yellow>"+tag)
                .hoverEvent(HoverEvent.showText(Component.text("Click to spawn", NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/deu group spawn "+tag+" "+loadMethod.name()));
    }

    private static Component selectAnimation(String tag, LoadMethod loadMethod){
        return MiniMessage.miniMessage().deserialize("- <yellow>"+tag)
                .hoverEvent(HoverEvent.showText(Component.text("Click to select", NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/deu anim select "+tag+" "+loadMethod.name()));
    }
}
