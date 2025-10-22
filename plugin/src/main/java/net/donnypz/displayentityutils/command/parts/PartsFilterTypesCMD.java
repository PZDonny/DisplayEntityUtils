package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

class PartsFilterTypesCMD extends PlayerSubCommand {

    private static final HashSet<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>(Arrays.stream(SpawnedDisplayEntityPart.PartType.values()).toList());

    PartsFilterTypesCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("filtertypes", parentSubCommand, Permission.PARTS_SELECT);
        setTabComplete(2, TabSuggestion.PART_TYPES);
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
            player.sendMessage(Component.text("/mdis parts filtertype <part-types>", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.empty());

        PartFilter builder = new PartFilter();

        if (args[2].equals("!")){
            player.sendMessage(Component.text("You cannot do that!", NamedTextColor.RED));
            return;
        }

        boolean exclude = args[2].startsWith("!");
        HashSet<SpawnedDisplayEntityPart.PartType> types = new HashSet<>();
        if (exclude) {
            types.addAll(partTypes);
            player.sendMessage(Component.text("Excluding Type(s):", NamedTextColor.RED));
        }
        else{
            player.sendMessage(Component.text("Including Type(s):", NamedTextColor.GREEN));
        }

        for (String typeText : args[2].split(",")){
            if (typeText.startsWith("!")){
                typeText = typeText.substring(1).toUpperCase();
            }
            else{
                typeText = typeText.toUpperCase();
            }

            try{
                if (!typeText.equals(SpawnedDisplayEntityPart.PartType.INTERACTION.name())){
                    typeText = typeText+"_DISPLAY";
                }

                SpawnedDisplayEntityPart.PartType type = SpawnedDisplayEntityPart.PartType.valueOf(typeText);

                if (exclude){
                    types.remove(type);
                }
                else{
                    types.add(type);
                }
            }
            catch(IllegalArgumentException e){
                player.sendMessage(Component.text("Invalid Part Type listed!", NamedTextColor.RED));
                player.sendMessage(Component.text("Valid Types: BLOCK, ITEM, TEXT, INTERACTION", NamedTextColor.GRAY, TextDecoration.ITALIC));
                return;
            }
            player.sendMessage(Component.text("- "+typeText, NamedTextColor.YELLOW));
        }
        if (types.isEmpty()){
            player.sendMessage(Component.text("You must have at least one part type filtered in!", NamedTextColor.RED));
            return;
        }

        builder.setPartTypes(types);
        player.sendMessage(Component.text());
        partSelection.unfilter(PartFilter.FilterType.PART_TYPE, false);

        if (!partSelection.applyFilter(builder, false)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to apply filter!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Part Type Filters Applied!")));
        if (!partSelection.hasSelectedParts()){
            player.sendMessage(Component.text("| Your filter does not apply to any parts", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        else{
            partSelection.glowAndMarkInteractions(player, 30);
        }
    }

}
