package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

class PartsInfoCMD extends PlayerSubCommand {
    PartsInfoCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("info", parentSubCommand, Permission.PARTS_INFO);
        setTabComplete(2, List.of("part", "selection", "filter"));
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }
        if (args.length < 3){
            incorrectUsage(player);
            return;
        }
        String type = args[2];
        boolean isSelection;
        if (type.equalsIgnoreCase("part")){
            isSelection = false;
        }
        else if (type.equalsIgnoreCase("selection") || type.equalsIgnoreCase("filter")){
            isSelection = true;
        }
        else{
            incorrectUsage(player);
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefixLong);


        //Spawned Part Selection
        if (isSelection){
            if (PartsCMD.isUnwantedSingleSelection(player, selection)){
                return;
            }
            MultiPartSelection<?> sel = (MultiPartSelection<?>) selection;
            ActiveGroup<?> group = sel.getGroup();
            String groupTag = group.getTag();

            groupTag = groupTag == null ? "<red>NOT SET" : "<yellow>"+groupTag;
            player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: "+groupTag));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Parts Selected: <yellow>"+sel.getSize()));
            sendFilterInfo(player, sel);
            return;
        }

        //Single Part
        ActivePart part = selection.getSelectedPart();
        player.sendMessage(MiniMessage.miniMessage().deserialize("Part Type: <yellow>"+part.getType()));


        if (part instanceof SpawnedDisplayEntityPart sp){
            UUID entityUUID = sp.getEntity().getUniqueId();
            player.sendMessage(MiniMessage.miniMessage().deserialize("Entity UUID: <yellow>"+entityUUID)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.copyToClipboard(entityUUID.toString())));
        }



        if (!selection.isSinglePartSelection()){
            UUID partUUID = part.getPartUUID();
            player.sendMessage(MiniMessage.miniMessage().deserialize("Part UUID: <yellow>"+partUUID)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.copyToClipboard(partUUID.toString())));

            player.sendMessage(MiniMessage.miniMessage().deserialize("Is Master Part: "+(part.isMaster() ? "<green>TRUE" : "<red>FALSE")));
            player.sendMessage(Component.empty());
        }
        if (part.isDisplay()){
            player.sendMessage(MiniMessage.miniMessage().deserialize("View Range Multiplier: <yellow>"+part.getViewRange()));
            sendBrightness(player, part);
            DEUCommandUtils.sendGlowColor(player, part.getGlowColor());
        }
        else if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            player.sendMessage(MiniMessage.miniMessage().deserialize("Height: <yellow>"+part.getInteractionHeight()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Width: <yellow>"+part.getInteractionWidth()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Responsive: "+(part.isInteractionResponsive() ? "<green>ENABLED" : "<red>DISABLED")));
        }
        else if (part.getType() == SpawnedDisplayEntityPart.PartType.MANNEQUIN){
            player.sendMessage(MiniMessage.miniMessage().deserialize("Immovable: <yellow>"+(part.isMannequinImmovable() ? "<green>ENABLED" : "<red>DISABLED")));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Gravity: <yellow>"+(part.hasMannequinGravity() ? "<green>ENABLED" : "<red>DISABLED")));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Pose: <yellow>"+part.getMannequinPose()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Scale: <yellow>"+part.getMannequinScale()));
        }

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("| Click to view Part Tags", NamedTextColor.GOLD)
                .clickEvent(ClickEvent.suggestCommand("/deu parts listtags")));

    }

    private void incorrectUsage(Player player){
        player.sendMessage(Component.text("Incorrect Usage! /deu parts info <part | selection>", NamedTextColor.RED));
    }

    private void sendBrightness(Player player, ActivePart part){
        if (!part.isDisplay()) return;
        Display.Brightness brightness = part.getBrightness();
        if (brightness == null){
            player.sendMessage(MiniMessage.miniMessage().deserialize("Brightness: <red>NOT SET"));
        }
        else{
            player.sendMessage(MiniMessage.miniMessage().deserialize("Brightness:"));
            player.sendMessage(Component.text("| Sky: "+brightness.getSkyLight(), NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Block: "+brightness.getBlockLight(), NamedTextColor.GRAY));
        }
    }

    private void sendFilterInfo(Player player, MultiPartSelection<?> sel){
        PartFilter filter = sel.toFilter();

        //Included Part Tags
        player.sendMessage(Component.text("Included Part Tags: "));
        HashSet<String> included = filter.getIncludedPartTags();
        if (included.isEmpty()){
            player.sendMessage(Component.text("- NOT SET", NamedTextColor.GRAY));
        }
        else{
            for (String s : included){
                player.sendMessage(Component.text(" - "+s, NamedTextColor.GRAY));
            }
        }

        //Excluded Part Tags
        player.sendMessage(Component.text("Excluded Part Tags: "));
        HashSet<String> excluded = filter.getExcludedPartTags();
        if (excluded.isEmpty()){
            player.sendMessage(Component.text("- NOT SET", NamedTextColor.GRAY));
        }
        else{
            for (String s : excluded){
                player.sendMessage(Component.text(" - "+s, NamedTextColor.GRAY));
            }
        }

        //Part Types
        player.sendMessage(Component.text("Included Part Types: "));
        HashSet<SpawnedDisplayEntityPart.PartType> partTypes = filter.getPartTypes();
        if (partTypes.isEmpty()){
            player.sendMessage(Component.text("- NOT SET", NamedTextColor.GRAY));
        }
        else{
            for (SpawnedDisplayEntityPart.PartType type : partTypes){
                player.sendMessage(Component.text(" - "+type, NamedTextColor.GRAY));
            }
        }

        //Item Types
        player.sendMessage(Component.text("Item Types: "));
        HashSet<Material> itemTypes = filter.getItemTypes();
        if (itemTypes.isEmpty()){
            player.sendMessage(Component.text("- NOT SET", NamedTextColor.GRAY));
        }
        else{
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>| Inclusive: "+(filter.isIncludingItemTypes() ? "<green>TRUE" : "<red>FALSE")));
            for (Material type : itemTypes){
                player.sendMessage(Component.text(" - "+type.key().value(), NamedTextColor.GRAY));
            }
        }

        //Block Types
        player.sendMessage(Component.text("Block Types: "));
        HashSet<Material> blockTypes = filter.getBlockTypes();
        if (blockTypes.isEmpty()){
            player.sendMessage(Component.text("- NOT SET", NamedTextColor.GRAY));
        }
        else{
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>| Inclusive: "+(filter.isIncludingBlockTypes() ? "<green>TRUE" : "<red>FALSE")));
            for (Material type : blockTypes){
                player.sendMessage(Component.text(" - "+type.key().value(), NamedTextColor.GRAY));
            }
        }
    }
}
