package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GizmoMoveHereCMD extends PlayerSubCommand {
    public GizmoMoveHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("movehere", parentSubCommand, Permission.GIZMO_USE);
    }

    @Override
    public void execute(Player player, String[] args) {
        DEUUser user = DEUUser.getOrCreateUser(player);
        ActivePartSelection<?> selection = user.getSelectedPartSelection();
        if (selection == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must have a group/entity selected to do this!", NamedTextColor.RED)));
            return;
        }

        World selWorld;
        if (selection.isSinglePartSelection()){
            selWorld = selection.getSelectedPart().getLocation().getWorld();
        }
        else{
            MultiPartSelection<?> multiSel = (MultiPartSelection<?>) selection;
            ActiveGroup<?> group = multiSel.getGroup();
            selWorld = Bukkit.getWorld(group.getWorldName());
        }

        if (!player.getWorld().equals(selWorld)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are not in the same world as your selection!", NamedTextColor.RED)));
            return;
        }

        GizmoSessionImpl gizmo = GizmoCMD.getOrCreateGizmo(player, null);
        if (GizmoCMD.isDraggingCancel(player, gizmo)) return;

        Location tpLoc = player.getLocation();
        tpLoc.setPitch(0);
        tpLoc.setYaw(0);
        gizmo.teleport(tpLoc);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved your Gizmo to your location", NamedTextColor.GREEN)));
    }

    @Override
    protected String getDescription() {
        return "";
    }
}
