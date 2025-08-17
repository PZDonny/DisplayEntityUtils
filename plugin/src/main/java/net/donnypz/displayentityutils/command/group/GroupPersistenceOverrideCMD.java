package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupPersistenceOverrideCMD extends PlayerSubCommand {
    GroupPersistenceOverrideCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("togglepersistoverride", parentSubCommand, Permission.GROUP_TOGGLE_PERSIST);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        boolean oldPersist = group.allowsPersistenceOverriding();
        group.setPersistenceOverride(!oldPersist);
        Component persist;
        if (oldPersist){
            persist = Component.text("DISABLED", NamedTextColor.RED);
        }
        else{
            persist = Component.text("ENABLED", NamedTextColor.GREEN);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Chunk Persistence Override: ", NamedTextColor.WHITE)).append(persist));

        if (!DisplayConfig.persistenceOverride()){
            player.sendMessage(Component.text("| \"automaticGroupDetection.persistenceOverride.enabled\" is false in the plugin's config", NamedTextColor.GRAY));
        }
    }
}
