package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.CMDUtils;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public final class BDEngineCMD extends PlayerSubCommand {

    private static final HashMap<String, PlayerSubCommand> subCommands = new HashMap<>();


    public BDEngineCMD(){
        super(Permission.HELP);
        subCommands.put("convertanim", new BDEngineConvertAnimCMD());
        subCommands.put("convertanimleg", new BDEngineConvertLegacyAnimCMD());
        subCommands.put("importmodel", new BDEngineImportModelCMD());
    }

    public static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2){
            conversionHelp(player);
            return;
        }
        String arg = args[1];
        PlayerSubCommand playerSubCommand = subCommands.get(arg);
        if (playerSubCommand == null){
            conversionHelp(player);
        }
        else{
            playerSubCommand.execute(player, args);
        }
    }

    static void conversionHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Use <yellow>\"block-display.com\" (BDEngine) <aqua>to create convertable models and animations"));
        sender.sendMessage(Component.empty());
        CMDUtils.sendCMD(sender,"/mdis bdengine convertanim <datapack-name> <group-tag-to-set> <anim-tag-prefix-to-set>",
                "Convert an animation datapack from BDEngine into a animation file usable for DisplayEntityUtils");
        CMDUtils.sendCMD(sender,"/mdis bdengine convertanimleg <datapack-name> <group-tag-to-set> <anim-tag-to-set>",
                "Convert an old animation datapack from BDEngine, before BDEngine v1.13 (Dec. 8th 2024), into a animation file usable for DisplayEntityUtils");
        sender.sendMessage(Component.empty());
        CMDUtils.sendCMD(sender, "/mdis bdengine importmodel <model-id>", "Import a model directly from BDEngine's Catalog into your game world");


    }


}
