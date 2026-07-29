package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class PartsCMD extends ParentSubCommand {

    public PartsCMD(){
        super("parts");
        new PartsInfoCMD(this);
        new PartsCreateCMD(this);
        new PartsSelectCMD(this);
        new PartsDeselectCMD(this);
        new PartsCycleCMD(this);
        new PartsCloneCMD(this);
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

    public static void invalidPartSelection(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Part Selection!", NamedTextColor.RED)));
        player.sendMessage(Component.text("| Try resetting/refreshing applied filters", NamedTextColor.GRAY));
        player.sendMessage(Component.text("| Or, try re-selecting a group or entity", NamedTextColor.GRAY));
    }

    public static void noPartSelected(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You do not have a part selected!", NamedTextColor.RED)));
        player.sendMessage(Component.text("| Try resetting/refreshing applied filters", NamedTextColor.GRAY));
    }

    public static boolean isUnwantedPacketPart(Player player, ActivePart part){
        if (part instanceof PacketDisplayEntityPart){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this with a packet-based part!", NamedTextColor.RED)));
            return true;
        }
        return false;
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
