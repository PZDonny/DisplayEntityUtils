package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PartsResetFilterCMD extends PlayerSubCommand {
    PartsResetFilterCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("resetfilter", parentSubCommand, Permission.PARTS_SELECT);
        setOptionalTabComplete(2, List.of("blocks", "items", "included_tags", "excluded_tags", "types"));
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        if (args.length < 3){
            ((MultiPartSelection<?>) sel).reset(true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Filter Reset!", NamedTextColor.YELLOW)));
            return;
        }

        String arg = args[2].toLowerCase();
        PartFilter.FilterType type = switch (arg) {
            case "blocks" -> PartFilter.FilterType.BLOCK_TYPE;
            case "items" -> PartFilter.FilterType.ITEM_TYPE;
            case "included_tags" -> PartFilter.FilterType.INCLUDED_TAGS;
            case "excluded_tags" -> PartFilter.FilterType.EXCLUDED_TAGS;
            case "types" -> PartFilter.FilterType.PART_TYPE;
            default -> null;
        };

        if (type == null){
            super.incorrectUsage(player);
            return;
        }
        ((MultiPartSelection<?>) sel).unfilter(type, true);
        player.sendMessage(DisplayAPI.pluginPrefix
                .append(MiniMessage
                        .miniMessage()
                        .deserialize("<yellow>Filter Type Reset! <white>(" + arg + ")")));
    }

    @Override
    protected String getDescription() {
        return "Reset your part selection's applied filters";
    }
}
