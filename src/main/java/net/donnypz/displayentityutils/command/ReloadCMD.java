package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.MachineState;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

class ReloadCMD implements ConsoleUsableSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.RELOAD)){
            return;
        }

        if (args.length < 2){
            sender.sendMessage(Component.text("Incorrect Usage! /mdis reload <config | controllers>", NamedTextColor.RED));
            return;
        }
        if (args[1].equals("controllers")){
            DisplayEntityPlugin.getInstance().reloadControllers();
            MachineState.registerNullLoaderStates();
            DisplayController.registerNullLoaderControllers();
            sender.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Attempted to reload Display Controllers! Ensure console does not contain any errors.", NamedTextColor.YELLOW)));
            sender.sendMessage(Component.text("| Existing Display Controllers may not have all changes applied a until server restart.", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        else if (args[1].equals("config")){
            DisplayEntityPlugin.getInstance().reloadPlugin(false);
            sender.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Plugin Config Reloaded!", NamedTextColor.YELLOW)));
        }
        else{
            sender.sendMessage(Component.text("Incorrect Usage! /mdis reload <config | controllers>", NamedTextColor.RED));
        }

    }
}
