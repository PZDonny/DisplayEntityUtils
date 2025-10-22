package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class BDEngineCMD extends ConsoleUsableSubCommand {

    public BDEngineCMD(){
        super(Permission.HELP, new BDEngineHelpCMD());
        new BDEngineConvertDatapackCMD(this);
        new BDEngineConvertLegacyDatapackCMD(this);
        new BDEngineImportModelCMD(this);
        new BDEngineSpawnModelCMD(this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            help(sender, 1);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            help(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void help(CommandSender sender, int page){
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Use <yellow>\"block-display.com\" (BDEngine) <aqua>to create convertable models and animations"));
        sender.sendMessage(Component.empty());
        CMDUtils.sendCMD(sender, "/mdis bdengine help", "Get help with BDEngine commands");
        CMDUtils.sendCMD(sender, "/mdis bdengine spawnmodel <file-name>", "Spawn a model from a BDEngine file located in the plugin's \"bdenginefiles\" folder");
        CMDUtils.sendCMD(sender, "/mdis bdengine importmodel <model-id>", "Import a model directly from BDEngine's Catalog into your game world");
        CMDUtils.sendCMD(sender, "/mdis bdengine convertdp <datapack-name> <group-tag-to-set> <anim-tag-prefix-to-set>",
                "Convert a datapack from BDEngine into group and animation files usable for DisplayEntityUtils");
        CMDUtils.sendCMD(sender, "/mdis bdengine convertdpleg <datapack-name> <group-tag-to-set> <anim-tag-to-set>",
                "Convert an old datapack from BDEngine, before BDEngine v1.13 (Dec. 8th 2024), into group and animation files usable for DisplayEntityUtils");
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>--------------------------"));
    }
}
