package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.RelativePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SinglePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.*;

public class DisplayEntitySelector extends RelativePointSelector<RelativePoint> {

    Display display;

    DisplayEntitySelector(Player player, Display display){
        super(player,
                DisplayUtils.getModelLocation(display),
                null,
                Material.LIME_STAINED_GLASS,
                display.getTransformation());
        this.display = display;
    }

    @Override
    public boolean removeFromPointHolder() {
        return true;
    }

    @Override
    public void sendInfo(Player player) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("Entity Type: <yellow>" + display.getType().name()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Persistent: "+(display.isPersistent() ? "<green>TRUE" : "<red>FALSE")));
    }

    @Override
    public void rightClick(Player player) {
        DisplayAPI.getScheduler().run(() -> {
            select(player, display);
        });
    }

    public static void select(Player player, Entity entity){
        SpawnedDisplayEntityPart existing = SpawnedDisplayEntityPart.getPart(entity);
        if (existing != null){
            player.sendMessage(Component.text("That part is already in a group!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Select the group, then cycle through the group's parts", NamedTextColor.GRAY));
            return;
        }

        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.create(entity.getUniqueId());
        if (part == null){
            player.sendMessage(Component.text("That entity is no longer valid!", NamedTextColor.RED));
            return;
        }
        DEUUser
                .getOrCreateUser(player)
                .setSelectedPartSelection(new SinglePartSelection(part), false);
        part.glow(player, 30);
        RelativePointUtils.removeRelativePoints(player);
        String entityType;
        switch(entity){
            case Display d -> {
                entityType = "Display";
            }
            case Interaction i -> {
                entityType = "Interaction";
            }
            case Mannequin m -> {
                entityType = "Mannequin";
            }
            case Shulker s -> {
                entityType = "Shulker";
            }
            default -> entityType = "";
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text(entityType+" Entity Selected!", NamedTextColor.GREEN)));
    }
}
