package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

class PartsFilterTagsCMD extends PlayerSubCommand {
    PartsFilterTagsCMD() {
        super(Permission.PARTS_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);

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
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to apply filter!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Part Tag Filters Applied!")));
        partSelection.glow(player, 30);
    }

}
