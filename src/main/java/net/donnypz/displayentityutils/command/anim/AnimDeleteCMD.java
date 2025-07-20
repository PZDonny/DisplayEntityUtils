package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimDeleteCMD extends PlayerSubCommand {
    AnimDeleteCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("delete", parentSubCommand, Permission.ANIM_DELETE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis anim delete <anim-tag> <storage-location>", NamedTextColor.RED));
            return;
        }

        String tag = args[2];

        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Attempting to delete display animation <white>(Tagged: "+tag+")")));
        switch(args[3].toLowerCase()){
            case "all" ->{
                DisplayAnimationManager.deleteDisplayAnimation(LoadMethod.LOCAL, tag, player);
                DisplayAnimationManager.deleteDisplayAnimation(LoadMethod.MONGODB, tag, player);
                DisplayAnimationManager.deleteDisplayAnimation(LoadMethod.MYSQL, tag, player);
            }
            case "local" -> {
                DisplayAnimationManager.deleteDisplayAnimation(LoadMethod.LOCAL, tag, player);
            }
            case "mongodb" -> {
                DisplayAnimationManager.deleteDisplayAnimation(LoadMethod.MONGODB, tag, player);
            }
            case "mysql" -> {
                DisplayAnimationManager.deleteDisplayAnimation(LoadMethod.MYSQL, tag, player);
            }
            default ->{
                player.sendMessage(Component.text("Invalid storage option!", NamedTextColor.RED));
            }
        }
    }
}
