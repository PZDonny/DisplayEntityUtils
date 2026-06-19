package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class BDEngineConvertDatapackCMD extends PlayerSubCommand {
    BDEngineConvertDatapackCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("convertdp", parentSubCommand, Permission.BDENGINE_CONVERT_DATAPACK);
        setTabComplete(2, "<datapack-name>");
        setTabComplete(3, "<group-tag-to-set>");
        setTabComplete(4, "<anim-tag-prefix-to-set>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) {
            player.sendMessage(Component.text("Use \"-\" for a tag if you do not want to save the group/animation(s)", NamedTextColor.GRAY));
            return;
        }

        String datapackName = args[2];
        String groupTag = args[3];
        String animPrefix = args[4];
        boolean saveGroups = !groupTag.equals("-");
        boolean saveAnimations = !animPrefix.equals("-");
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Attempting to convert datapack...", NamedTextColor.AQUA)));
        player.sendMessage(Component.text(" | DO NOT LEAVE THIS AREA UNTIL COMPLETION!", NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" | Conversion times may vary.", NamedTextColor.YELLOW));
        BDEngineUtils.convertDatapack(
                datapackName,
                player,
                !saveGroups ? "" : groupTag,
                !saveAnimations ? "" : animPrefix,
                saveGroups,
                saveAnimations,
                true
        );
    }

    @Override
    protected String getDescription() {
        return "Convert BDEngine datapack into group and animation formats this plugin uses";
    }
}
