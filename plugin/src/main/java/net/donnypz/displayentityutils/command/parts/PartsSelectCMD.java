package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.SinglePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

class PartsSelectCMD extends PlayerSubCommand {

    PartsSelectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("select", parentSubCommand, Permission.PARTS_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts select <distance>", NamedTextColor.RED));
            return;
        }
        try{
            double distance = Double.parseDouble(args[2]);
            if (distance <= 0){
                throw new IllegalArgumentException();
            }
            player.sendMessage(Component.text("Finding entities within "+distance+" blocks...", NamedTextColor.YELLOW));
            getSelectableEntities(player, distance);
        }
        catch(IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid distance! The distance must be a positive number.", NamedTextColor.RED));
        }
    }

    private void getSelectableEntities(Player player, double distance){
        List<Entity> parts = DisplayUtils.getUngroupedPartEntities(player.getLocation(), distance);
        if (parts.isEmpty()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("No nearby ungrouped entities found!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Ensure that any nearby ungrouped entities do not have passengers", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return;
        }

        player.sendMessage(Component.text("| Entities found! Click to select.", NamedTextColor.GREEN).appendNewline());
        if (parts.size() == 1){
            select(player, parts.getFirst().getUniqueId());
            player.sendMessage(PartsCycleCMD.getPartInfo(parts.getFirst()).color(NamedTextColor.GRAY));
            return;
        }

        for (Entity e : parts){
            UUID entityUUID = e.getUniqueId();
            SpawnedDisplayEntityPart.PartType partType = SpawnedDisplayEntityPart.PartType.getDisplayType(e);
            String coords = ConversionUtils.getCoordinateString(e.getLocation());
            Component comp = Component.text("- "+partType.name()+": ")
                    .append(PartsCycleCMD.getPartInfo(e))
                    .hoverEvent(HoverEvent.showText(Component.text("Location: ", NamedTextColor.AQUA).append(Component.text(coords, NamedTextColor.YELLOW))))
                    .clickEvent(ClickEvent.callback(audience -> {
                        Player p = (Player) audience;
                        select(p, entityUUID);
                    }, ClickCallback.Options.builder().uses(ClickCallback.UNLIMITED_USES).lifetime(Duration.ofMinutes(10)).build()));
            player.sendMessage(comp);
        }
    }

    private void select(Player player, UUID entityUUID){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.create(entityUUID);
        if (part == null){
            player.sendMessage(Component.text("That entity is no longer valid!", NamedTextColor.RED));
            return;
        }
        DEUUser
                .getOrCreateUser(player)
                .setSelectedPartSelection(new SinglePartSelection(part), false);
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            part.markInteraction(player, 30);
        }
        else{
            part.glow(player, 30);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Part Entity Selected!", NamedTextColor.GREEN)));
    }
}
