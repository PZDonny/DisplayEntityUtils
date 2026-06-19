package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PartsCMD extends ParentSubCommand {

    public PartsCMD(){
        super("parts");
        new PartsInfoCMD(this);
        new PartsCreateCMD(this);
        new PartsSelectCMD(this);
        new PartsDeselectCMD(this);
        new PartsCycleCMD(this);
        new PartsGlowCMD(this);
        new PartsUnglowCMD(this);
        new PartsFilterTagsCMD(this);
        new PartsFilterTypesCMD(this);
        new PartsFilterBlocksCMD(this);
        new PartsFilterItemsCMD(this);
        new PartsRefreshFilterCMD(this);
        new PartsResetFilterCMD(this);
        new PartsAdaptTagsCMD(this);
        new PartsAddTagCMD(this);
        new PartsRemoveTagCMD(this);
        new PartsListTagsCMD(this);
        new PartsRemoveCMD(this);
        new PartsPitchCMD(this);
        new PartsYawCMD(this);
        new PartsMoveHereCMD(this);
        new PartsMoveCMD(this);
    }

    public static void invalidPartSelection(CommandSender sender){
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your part selection is invalid!", NamedTextColor.RED)));
    }

    public static boolean isUnwantedMultiSelection(Player player, ActivePartSelection<?> selection){
        if (selection instanceof MultiPartSelection){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this with a grouped part!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }

    public static boolean isUnwantedSingleSelection(Player player, ActivePartSelection<?> selection){
        if (selection.isSinglePartSelection()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this with an ungrouped selected part entity!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }

    public static boolean isUnwantedSingleSelectionAll(Player player, ActivePartSelection<?> selection){
        if (selection.isSinglePartSelection()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot use \"-all\" with an ungrouped selected part entity!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }
}
