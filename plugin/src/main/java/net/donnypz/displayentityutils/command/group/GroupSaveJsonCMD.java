package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupSaveJsonCMD extends PlayerSubCommand {
    GroupSaveJsonCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("save", parentSubCommand, Permission.GROUP_SAVE);
        setTabComplete(2, TabSuggestion.STORAGES);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (group.getTag() == null){
            player.sendMessage(Component.text("Failed to save display entity group, no tag provided!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Use \"/mdis group settag <tag>\"", NamedTextColor.GRAY));
            return;
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Attempting to save your selected group as <light_purple>JSON <white>(Tagged: "+group.getTag()+")")));
        DisplayEntityGroup displayGroup = group.toDisplayEntityGroup();
        DisplayAPI.getScheduler().runAsync(() -> {
            DisplayGroupManager.saveDisplayEntityGroupJson(displayGroup, player);
        });
    }
}
