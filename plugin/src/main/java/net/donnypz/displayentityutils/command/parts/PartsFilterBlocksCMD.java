package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

class PartsFilterBlocksCMD extends PlayerSubCommand {


    PartsFilterBlocksCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("filterblocks", parentSubCommand, Permission.PARTS_SELECT);
        setTabComplete(2, "<block-ids>");
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = (SpawnedPartSelection) sel;
        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts filterblocks <block-ids>", NamedTextColor.RED));
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
            player.sendMessage(Component.text("Including Block(s):", NamedTextColor.GREEN));
        }
        else{
            player.sendMessage(Component.text("Excluding Block(s):", NamedTextColor.RED));
        }

        for (String typeText : args[2].split(",")){
            Material material;
            if (typeText.startsWith("!")){
                material = Material.matchMaterial(typeText.toLowerCase().substring(1));
            }
            else{
                material = Material.matchMaterial(typeText.toLowerCase());
            }

            if (material == null || !material.isBlock()){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Block not recognized! The block's name might have been misspelled or the block doesn't exist.", NamedTextColor.RED)));
                return;
            }
            types.add(material);
            player.sendMessage(Component.text("- "+material, NamedTextColor.YELLOW));
        }

        builder.setBlockTypes(types, include);
        player.sendMessage(Component.text());
        partSelection.unfilter(PartFilter.FilterType.BLOCK_TYPE, false);

        if (!partSelection.applyFilter(builder, false)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to apply filter!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Block Type Filters Applied!")));
        player.sendMessage(Component.text("This has no effect if Block Displays are filtered out of your selection", NamedTextColor.GRAY, TextDecoration.ITALIC));
        if (!partSelection.hasSelectedParts()){
            player.sendMessage(Component.text("| Your filter does not apply to any parts", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        else{
            partSelection.glow(player, 30);
        }
    }

}
