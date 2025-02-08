package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group Teleportation Duration")
@Description("Get or Set the teleporation duration of a spawned group")
@Examples({"set {_spawnedgroup}'s teleporation duration to 5 ticks", "reset {_spawnedgroup}'s teleportation duration"})
@Since("2.6.2")
public class ExprSpawnedTeleportDuration extends SimplePropertyExpression<SpawnedDisplayEntityGroup, Number> {

    static {
        register(ExprSpawnedTeleportDuration.class, Number.class, "[the] [deu] teleport[ation][ |-]duration", "spawnedgroup");
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    @Nullable
    public Number convert(SpawnedDisplayEntityGroup group) {
        if (group == null){
            return null;
        }

        return group.getTeleportDuration();
    }

    @Override
    protected String getPropertyName() {
        return "teleportduration";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        SpawnedDisplayEntityGroup group = getExpr().getSingle(event);
        if (group == null){
            return;
        }

        switch (mode) {
            case SET -> {
                if (delta == null){
                    return;
                }
                Timespan timespan = (Timespan) delta[0];
                group.setTeleportDuration((int) timespan.getAs(Timespan.TimePeriod.TICK));
            }
            case RESET -> {
                group.setTeleportDuration(0);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Color.class);
        }
        return null;
    }
}
