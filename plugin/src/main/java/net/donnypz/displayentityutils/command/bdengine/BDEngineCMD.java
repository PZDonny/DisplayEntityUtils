package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class BDEngineCMD extends ParentSubCommand{

    public BDEngineCMD(){
        super("bdengine");
        new BDEngineConvertDatapackCMD(this);
        new BDEngineImportCMD(this);
        new BDEngineSpawnModelCMD(this);
    }

    static void help(CommandSender sender, int page){
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Use <yellow>\"block-display.com\" (BDEngine) <aqua>to create convertable models and animations"));
        sender.sendMessage(Component.empty());
        CMDUtils.sendCMD(sender, "/deu bdengine help", "Get help with BDEngine commands");
        CMDUtils.sendCMD(sender, "/deu bdengine spawnmodel <file-name>", "Spawn a model from a BDEngine file located in the plugin's \"bdenginefiles\" folder");
        CMDUtils.sendCMD(sender, "/deu bdengine import <project-id> <group-tag-to-set> <anim-prefix-to-set>", "Import and convert a BDEngine project's model and animations into your game world");
        CMDUtils.sendCMD(sender, "/deu bdengine convertdp <datapack-name> <group-tag-to-set> <anim-tag-prefix-to-set>",
                "Convert BDEngine datapack into group and animation formats this plugin uses");
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>--------------------------"));
    }
}
