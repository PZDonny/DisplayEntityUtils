package net.donnypz.displayentityutils.skript.active.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group/Part/Filter View Range")
@Description("Get/Set the view range of an active group/part/filter")
@Examples({
        "set deu view range of {_activepart} to 5",
        "set {_activegroup}'s deu view range multiplier to 2",
        "",
        "#The view range can only be retrieved from an active part",
        "set {_delay} to {_activepart}'s deu view range",
        "",
        "#3.4.3 and earlier",
        "set {_activegroup}'s view range multiplier to 5",
        "make {_activepart}'s view range multiplier 0.5"
})
@Since("2.6.2, 3.0.0 (Packet)")
public class ExprActiveViewRange extends SimplePropertyExpression<Active, Number> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveViewRange.class, Number.class)
                        .addPatterns(getPatterns("deu view range [multiplier]", "activegroups/activeparts/partfilters"))
                        .supplier(ExprActiveViewRange::new)
                        .build()
        );
    }

    @Nullable
    @Override
    public Number convert(Active active) {
        if (active instanceof ActivePart ap){
            return ap.getViewRange();
        }
        return null;
    }


    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "deu view range";
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){

        switch (mode) {
            case SET -> {
                if (delta == null) return;

                Number range = (Number) delta[0];

                for (Active a : getExpr().getArray(event)){
                    a.setViewRange(range.intValue());
                }
            }
            case RESET -> {
                for (Active a : getExpr().getArray(event)){
                    a.setViewRange(1);
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
