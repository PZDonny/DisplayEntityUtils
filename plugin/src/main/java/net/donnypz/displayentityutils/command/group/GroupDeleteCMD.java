package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupDeleteCMD extends PlayerSubCommand {
    GroupDeleteCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("delete", parentSubCommand, Permission.GROUP_DELETE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis group delete <anim-tag> <storage-location>", NamedTextColor.RED));
            return;
        }

        String tag = args[2];
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Attempting to delete display entity group <white>(Tagged: "+tag+")")));
        Bukkit.getScheduler().runTaskAsynchronously(DisplayAPI.getPlugin(), () -> {
            switch(args[3].toLowerCase()){
                case "all" ->{
                    DisplayGroupManager.deleteDisplayEntityGroup(LoadMethod.LOCAL, tag, player);
                    DisplayGroupManager.deleteDisplayEntityGroup(LoadMethod.MONGODB, tag, player);
                    DisplayGroupManager.deleteDisplayEntityGroup(LoadMethod.MYSQL, tag, player);
                }
                case "local" -> {
                    DisplayGroupManager.deleteDisplayEntityGroup(LoadMethod.LOCAL, tag, player);
                }
                case "mongodb" -> {
                    DisplayGroupManager.deleteDisplayEntityGroup(LoadMethod.MONGODB, tag, player);
                }
                case "mysql" -> {
                    DisplayGroupManager.deleteDisplayEntityGroup(LoadMethod.MYSQL, tag, player);
                }
                default ->{
                    player.sendMessage(Component.text("Invalid storage option!", NamedTextColor.RED));
                }
            }
        });
    }
}
