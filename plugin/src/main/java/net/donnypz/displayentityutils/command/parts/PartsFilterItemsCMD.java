package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

class PartsFilterItemsCMD extends PlayerSubCommand {


    PartsFilterItemsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("filteritems", parentSubCommand, Permission.PARTS_SELECT);
        setTabComplete(2, "<item-ids>");
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        MultiPartSelection<?> partSelection = (MultiPartSelection<?>) sel;
        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts filteritems <items-ids>", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.empty());

        PartFilter builder = new PartFilter();

        if (args[2].equals("!")){
            player.sendMessage(Component.text("You cannot do that!", NamedTextColor.RED));
            return;
        }

        boolean include = !args[2].startsWith("!");

        HashSet<Material> types = new HashSet<>();
        if (include) {
            player.sendMessage(Component.text("Including Items(s):", NamedTextColor.GREEN));
        }
        else{
            player.sendMessage(Component.text("Excluding Items(s):", NamedTextColor.RED));
        }

        for (String typeText : args[2].split(",")){
            Material material;
            if (typeText.startsWith("!")){
                material = Material.matchMaterial(typeText.toLowerCase().substring(1));
            }
            else{
                material = Material.matchMaterial(typeText.toLowerCase());
            }

            if (material == null){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Item not recognized! The item's name might have been misspelled or the item doesn't exist.", NamedTextColor.RED)));
                return;
            }
            types.add(material);
            player.sendMessage(Component.text("- "+material, NamedTextColor.YELLOW));
        }

        builder.setItemTypes(types, include);
        player.sendMessage(Component.text());
        partSelection.unfilter(PartFilter.FilterType.ITEM_TYPE, false);

        if (!partSelection.applyFilter(builder, false)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to apply filter!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Item Type Filters Applied!")));
        player.sendMessage(Component.text("This has no effect if Item Displays are filtered out of your selection", NamedTextColor.GRAY, TextDecoration.ITALIC));
        if (!partSelection.hasSelectedParts()){
            player.sendMessage(Component.text("| Your filter does not apply to any parts", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        else{
            partSelection.glow(player, 30);
        }
    }

}
