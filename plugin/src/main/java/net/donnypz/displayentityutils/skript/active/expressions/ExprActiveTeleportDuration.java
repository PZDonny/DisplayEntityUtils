package net.donnypz.displayentityutils.skript.active.expressions;

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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group/Part/Filter Teleport Duration")
@Description("Get/Set the teleportation duration of an active group, part or parts in a part filter. Get the duration on an active group/part.")
@Examples({
        "#Set",
        "set {_activegroup}'s deu teleport duration to 5 ticks",
        "",
        "#Reset",
        "reset {_active}'s deu teleport duration"})
@Since("2.7.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActiveTeleportDuration extends SimplePropertyExpression<Active, Number> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveTeleportDuration.class, Number.class)
                        .addPatterns(getPatterns("deu teleport[ation][ |-]duration", "activegroups/activeparts/multipartfilters"))
                        .supplier(ExprActiveTeleportDuration::new)
                        .build()
        );
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    @Nullable
    public Number convert(Active active) {
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
        return "deu teleport duration";
    }


    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Active[] active = getExpr().getArray(event);
        if (active == null){
            return;
        }

        switch (mode) {
            case SET -> {
                if (delta == null) return;

                Timespan timespan = (Timespan) delta[0];
                int duration = (int) timespan.getAs(Timespan.TimePeriod.TICK);

                for (Active a : getExpr().getArray(event)){
                    a.setTeleportDuration(duration);
                }
            }
            case RESET -> {
                for (Active a : getExpr().getArray(event)){
                    a.setTeleportDuration(0);
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Timespan.class);
        }
        return null;
    }
}
