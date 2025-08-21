package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsFilterTagsCMD extends PlayerSubCommand {
    PartsFilterTagsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("filtertags", parentSubCommand, Permission.PARTS_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ServerSideSelection sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = (SpawnedPartSelection) sel;
        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts filtertags <part-tags>", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.empty());
        PartFilter builder = new PartFilter();
        if (args[2].equals("!")){
            player.sendMessage(Component.text("You cannot do that!"));
            return;
        }
        for (String tag : args[2].split(",")){
            if (!tag.startsWith("!")){
                builder.includePartTag(tag);
                player.sendMessage(Component.text("Including tag: "+tag, NamedTextColor.GREEN));
            }
            else{
                String excludedTag = tag.substring(1);
                builder.excludePartTag(excludedTag);
                player.sendMessage(Component.text("Excluding tag: "+excludedTag, NamedTextColor.RED));
            }
        }

        partSelection.unfilter(PartFilter.FilterType.INCLUDED_TAGS, false);
        partSelection.unfilter(PartFilter.FilterType.EXCLUDED_TAGS, false);

        if (!partSelection.applyFilter(builder, false)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to apply filter!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Part Tag Filters Applied!")));
        partSelection.glow(player, 30);
    }

}
