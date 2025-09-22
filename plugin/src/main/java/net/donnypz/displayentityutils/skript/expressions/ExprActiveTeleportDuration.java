package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group/Part/Part Selection Teleportation Duration")
@Description("Set the teleportation duration of an active group, part or part selection. Get the duration on an active group or active part object.")
@Examples({"set {_spawnedgroup}'s deu teleportation duration to 5 ticks",
        "reset {_spawnedgroup}'s deu teleport duration",
        "",
        "#3.0.0 and later",
        "set {_packetgroup}'s deu teleport duration to 1 tick"})
@Since("2.7.2")
public class ExprActiveTeleportDuration extends SimplePropertyExpression<Active, Number> {

    static {
        register(ExprActiveTeleportDuration.class, Number.class, "[deu] teleport[ation][ |-]duration", "activegroups/activeparts/multipartselections");
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    @Nullable
    public Number convert(Active active) {
        if (active == null){
            return null;
        }
        if (active instanceof ActiveGroup ag){
            return ag.getTeleportDuration();
        }
        if (active instanceof ActivePart ap){
            return ap.getTeleportDuration();
        }

        return null;
    }

    @Override
    protected String getPropertyName() {
        return "teleport duration";
    }


    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Active active = getExpr().getSingle(event);
        if (active == null){
            return;
        }

        switch (mode) {
            case SET -> {
                if (delta == null){
                    return;
                }
                Timespan timespan = (Timespan) delta[0];
                active.setTeleportDuration((int) timespan.getAs(Timespan.TimePeriod.TICK));
            }
            case RESET -> active.setTeleportDuration(0);
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Timespan.class);
        }
        return null;
    }
}
