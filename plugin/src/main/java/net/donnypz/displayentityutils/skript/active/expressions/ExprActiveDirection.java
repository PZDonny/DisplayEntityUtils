package net.donnypz.displayentityutils.skript.active.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Direction (Pitch, Yaw, Pivot)")
@Description("Get/Set the pitch and yaw of an active group/part/filter. Optionally pivot interactions")
@Examples({
        "set {_activegroup}'s deu pitch to -45",
        "set {_activegroup}'s deu yaw to 73",
        "set {_activegroup}'s deu yaw with interaction pivot to 25",
        "",
        "#3.4.3 and earlier",
        "deu set {_activegroup}'s yaw with interaction pivot to 35",
        "deu set {_activepart}'s pitch to -90"})
@Since("2.6.2, 3.0.0 (Packet)")
public class ExprActiveDirection extends SimplePropertyExpression<Active, Number> {

    boolean pitch;
    boolean pivot;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveDirection.class, Number.class)
                        .addPatterns(getPatterns("deu (1¦yaw [p:with [interaction] pivot]|2¦pitch)", "activegroups/activeparts/partfilters"))
                        .supplier(ExprActiveDirection::new)
                        .build()
        );
    }

    @Override
    public Number convert(Active from) {
        if (from instanceof ActivePart part){
            return pitch ? part.getPitch() : part.getYaw();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "deu direction";
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Object[] arr = getExpr().getArray(event);
        if (arr == null){
            return;
        }

        switch (mode) {
            case SET -> {
                if (delta == null) return;

                Number value = (Number) delta[0];
                if (value == null) return;
                float rotVal = value.floatValue();

                for (Object o : arr){
                    if (!(o instanceof Active a)) continue;
                    if (pitch){
                        a.setPitch(rotVal);
                    }
                    else{
                        a.setYaw(rotVal, pivot);
                    }
                }
            }
            case RESET -> {
                for (Object o : arr){
                    if (!(o instanceof Active a)) continue;
                    if (pitch){
                        a.setPitch(0);
                    }
                    else{
                        a.setYaw(0, pivot);
                    }
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

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pitch = parseResult.mark == 2;
        if (!pitch){
            pivot = parseResult.hasTag("p");
        }
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }
}
