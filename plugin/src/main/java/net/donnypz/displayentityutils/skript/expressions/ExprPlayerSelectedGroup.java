package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Player's Selected Spawned Group")
@Description("Get/Set the selected spawned group of a player")
@Examples({
        "#Get selected group",
        "set {_spawnedgroup} to player's selected spawned group",
        "",
        "#Set selected group",
        "set player's selected group to {_spawnedgroup}",
        "",
        "#Reset group selection",
        "reset player's selected spawned group"
        })
@Since("2.6.2, 3.3.2 (Plural)")
public class ExprPlayerSelectedGroup extends SimplePropertyExpression<Player, SpawnedDisplayEntityGroup> {
    static {
        register(ExprPlayerSelectedGroup.class, SpawnedDisplayEntityGroup.class, "selected [spawned[ |-]]group", "players");
    }

    @Override
    public Class<? extends SpawnedDisplayEntityGroup> getReturnType() {
        return SpawnedDisplayEntityGroup.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityGroup convert(Player player) {;
        if (player != null){
            return DisplayGroupManager.getSelectedSpawnedGroup(player);
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "selected spawned group";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Player p = getExpr().getSingle(event);
        if (p == null){
            return;
        }


        switch (mode) {
            case SET -> {
                if (delta == null){
                    return;
                }
                SpawnedDisplayEntityGroup deltaGroup = (SpawnedDisplayEntityGroup) delta[0];
                deltaGroup.addPlayerSelection(p);
            }
            case RESET -> {
                DisplayGroupManager.deselectSpawnedGroup(p);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(SpawnedDisplayEntityGroup.class);
        }
        return null;
    }
}
