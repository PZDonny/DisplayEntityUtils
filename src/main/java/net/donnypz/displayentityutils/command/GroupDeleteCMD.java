package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

class GroupDeleteCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_DELETE)){
            return;
        }

        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis group delete <anim-tag> <storage-location>", NamedTextColor.RED));
            return;
        }

        String tag = args[2];
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Attempting to delete display entity group <white>(Tagged: "+tag+")")));
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
    }
}
