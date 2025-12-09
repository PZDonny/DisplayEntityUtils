package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.ConfigUtils;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

class ReloadCMD extends ConsoleUsableSubCommand {
    ReloadCMD() {
        super(Permission.RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            sender.sendMessage(Component.text("Incorrect Usage! /deu reload <config | controllers>", NamedTextColor.RED));
            return;
        }
        if (args[1].equals("controllers")){
            ConfigUtils.registerDisplayControllers();
            MachineState.registerNullLoaderStates();
            DisplayControllerUtils.registerNullLoaderControllers();
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Attempted to reload Display Controllers! Ensure console does not contain any errors.", NamedTextColor.YELLOW)));
            sender.sendMessage(Component.text("| Existing Display Controllers may not have all changes applied a until server restart.", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        else if (args[1].equals("config")){
            DisplayEntityPlugin.reloadPlugin(false);
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Plugin Config Reloaded!", NamedTextColor.YELLOW)));
        }
        else{
            sender.sendMessage(Component.text("Incorrect Usage! /deu reload <config | controllers>", NamedTextColor.RED));
        }

    }
}
