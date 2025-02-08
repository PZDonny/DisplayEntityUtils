package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;

class PartsFilterTypesCMD implements PlayerSubCommand {

    private static final HashSet<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>(Arrays.stream(SpawnedDisplayEntityPart.PartType.values()).toList());

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_SELECT)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);

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
            try{
                if (!typeText.equalsIgnoreCase("interaction")){
                    typeText = typeText+"_display";
                }
                SpawnedDisplayEntityPart.PartType type;
                if (typeText.startsWith("!")){
                    type = SpawnedDisplayEntityPart.PartType.valueOf(typeText.substring(1).toUpperCase());
                }
                else{
                    type = SpawnedDisplayEntityPart.PartType.valueOf(typeText.toUpperCase());
                }
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
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to apply filter!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Part Type Filters Applied!")));
        partSelection.glow(30, false, false);
    }

}
