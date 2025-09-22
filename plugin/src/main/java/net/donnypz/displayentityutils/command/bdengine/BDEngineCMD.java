package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class BDEngineCMD extends PlayerSubCommand {

    public BDEngineCMD(){
        super(Permission.HELP, true);
        new BDEngineDatapackAnimCMD(this);
        new BDEngineDatapackLegacyAnimCMD(this);
        new BDEngineImportModelCMD(this);
        new BDEngineConvertFileCMD(this);
        new BDEngineConvertModelCMD(this);
        new BDEngineConvertAnimCMD(this);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2){
            conversionHelp(player);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            conversionHelp(player);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, player, args);
        }
    }

    static void conversionHelp(CommandSender sender){
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Use <yellow>\"block-display.com\" (BDEngine) <aqua>to create convertible models and animations"));
        sender.sendMessage(Component.empty());
        CMDUtils.sendCMD(sender, "/mdis bdengine help", "Get help with BDEngine commands");
        CMDUtils.sendCMD(sender, "/mdis bdengine importmodel <model-id>", "Import a model directly from BDEngine's Catalog into your game world");
        CMDUtils.sendCMD(sender, "/mdis bdengine convertfile <file-name> <group-tag> <anim-tag-prefix>", "Spawn a model and convert animations from a BDEngine file " +
                "location in the plugin's \"bdenginefiles\" folder");
        CMDUtils.sendCMD(sender, "/mdis bdengine convertmodel <file-name> <group-tag>", "Spawn a model from a BDEngine file located in the plugin's \"bdenginefiles\" folder");
        CMDUtils.sendCMD(sender, "/mdis bdengine convertanim <file-name> <anim-tag-prefix>", "Convert animations from a BDEngine file location in the plugin's \"bdenginefiles\" folder");
        CMDUtils.sendCMD(sender, "/mdis bdengine datapackanim <datapack-name> <group-tag-to-set> <anim-tag-prefix-to-set>",
                "Convert an animation datapack from BDEngine into a animation file usable for DisplayEntityUtils");
        CMDUtils.sendCMD(sender, "/mdis bdengine datapackanimleg <datapack-name> <group-tag-to-set> <anim-tag-to-set>",
                "Convert an old animation datapack from BDEngine, before BDEngine v1.13 (Dec. 8th 2024), into a animation file usable for DisplayEntityUtils");
        sender.sendMessage(Component.empty());
    }
}
