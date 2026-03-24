package net.donnypz.displayentityutils.skript.active.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group/Part/Filter Interpolation")
@Description("Get/Set the interpolation duration/delay of an active group/part/filter")
@Examples({"set deu interpolation duration of {_activepart} to 5 ticks",
        "set {_activegroup}'s deu interpolation delay to 2 ticks",
        "",
        "#The interpolation duration/delay can only be retrieved from an active part",
        "set {_delay} to {_activepart}'s deu interpolation delay",
        "",
        "#3.4.3 and earlier",
        "deu set interpolation duration of {_activepart} to 5 ticks",
        "deu set {_activegroup}'s interpolation delay to 2 ticks"
})
@Since("2.6.2, 3.0.0 (Packet)")
public class ExprActiveInterpolation extends SimplePropertyExpression<Active, Number> {

    boolean duration;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveInterpolation.class, Number.class)
                        .addPatterns(getPatterns("deu interpolation (:duration|delay)", "activegroups/activeparts/partfilters"))
                        .supplier(ExprActiveInterpolation::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        duration = parseResult.hasTag("duration");
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Nullable
    @Override
    public Number convert(Active active) {
        if (active instanceof ActivePart ap){
            return duration ? ap.getInterpolationDuration() : ap.getInterpolationDelay();
        }
        return null;
    }


    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "deu interpolation "+(duration ? "duration" : "delay");
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){

        switch (mode) {
            case SET -> {
                if (delta == null) return;

                Timespan timespan = (Timespan) delta[0];
                int value = (int) timespan.getAs(Timespan.TimePeriod.TICK);

                for (Active a : getExpr().getArray(event)){
                    if (this.duration){
                        a.setInterpolationDuration(value);
                    }
                    else{
                        a.setInterpolationDelay(value);
                    }
                }
            }
            case RESET -> {
                for (Active a : getExpr().getArray(event)){
                    if (duration){
                        a.setInterpolationDuration(0);
                    }
                    else{
                        a.setInterpolationDelay(0);
                    }
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
