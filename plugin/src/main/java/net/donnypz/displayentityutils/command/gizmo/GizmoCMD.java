package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ParentSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.GizmoManager;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSession;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class GizmoCMD extends ParentSubCommand {
    public GizmoCMD() {
        super("gizmo");
        new GizmoWandCMD(this);
        new GizmoScaleCMD(this);
        new GizmoToggleCMD(this);
        new GizmoMoveHereCMD(this);
        new GizmoResetCMD(this);
    }

    public static GizmoSessionImpl getOrCreateGizmo(Player player, Location spawnLocation) {
        DEUUser user = DEUUser.getOrCreateUser(player);
        GizmoSessionImpl gizmo = (GizmoSessionImpl) user.getGizmo();
        if (gizmo == null) {
            gizmo = new GizmoSessionImpl(player, spawnLocation);
            GizmoManager.setGizmo(user, gizmo);
        }
        return gizmo;
    }

    public static GizmoSessionImpl getGizmo(Player player){
        return (GizmoSessionImpl) DEUUser.getOrCreateUser(player).getGizmo();
    }

    //show gizmo on display/group selection
    public static void selectShowGizmo(Player player, @NotNull Location spawnLocation) {
        GizmoSessionImpl gizmo = getOrCreateGizmo(player, spawnLocation);
        gizmo.selectShow(spawnLocation);
        player.sendMessage(Component.text("|", NamedTextColor.GRAY)
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .append(Component.text("[TOGGLE GIZMO]", NamedTextColor.AQUA)
                        .hoverEvent(HoverEvent.showText(Component.text("Toggle Gizmo Visibility", NamedTextColor.YELLOW)))
                        .clickEvent(ClickEvent.suggestCommand("/deu gizmo toggle")))
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .append(Component.text("[GIZMO WAND]", NamedTextColor.LIGHT_PURPLE)
                        .hoverEvent(HoverEvent.showText(Component.text("Get a Gizmo Wand", NamedTextColor.YELLOW)))
                        .clickEvent(ClickEvent.suggestCommand("/deu gizmo wand"))));
        player.sendMessage(Component.empty());
        gizmo.updateRotation();
    }

    //hide gizmo on display/group deselection
    public static void deselectHideGizmo(Player player) {
        GizmoSessionImpl gizmo = getGizmo(player);
        if (gizmo != null) gizmo.deselectHide();
    }

    public static boolean isDraggingCancel(Player player, GizmoSession gizmo) {
        if (gizmo != null && gizmo.isDragging()) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do that while having a Gizmo axis selected.", NamedTextColor.RED)));
            return true;
        }
        return false;
    }

    static boolean hasRequiredSelection(Player player, GizmoSession gizmo){
        if (gizmo.hasSelection()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selection is invalid", NamedTextColor.RED)));
            return false;
        }
        return true;
    }

    public static void updateGizmoRotationIfExists(Player player){
        GizmoSessionImpl gizmo = (GizmoSessionImpl) DEUUser.getOrCreateUser(player).getGizmo();
        if (gizmo != null) gizmo.updateRotation();
    }
}
