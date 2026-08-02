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
import net.donnypz.displayentityutils.utils.Axis;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;


@Name("Active Group/Part - Rotation")
@Description("Get/Set the local, transformation space rotation of an active group or active part. " +
        "Vectors can be used to set the rotation, and its values must be in degrees.")
@Examples({
        "set {_activepart}'s deu y rotation to 90 degrees",
        "set {_activegroup}'s deu x rotation to 45 degrees",
        "",
        "set {_activegroup}'s deu rotation to {_vector}",
        "set {_activepart}'s deu rotation to {_quaternion}",
        "",
        "set {_quaternion} to {_activepart}'s deu rotation",
        "set {_quaternion} to {_activegroup}'s deu display rotation",
})
@Since("3.7.0")
public class ExprActiveRotation extends SimplePropertyExpression<Active, Quaternionf> {

    Axis axis;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveRotation.class, Quaternionf.class)
                        .addPatterns(getPatterns("deu [display|transform[ation]] [:x|:y|:z] rotation [degrees]", "activegroups/activeparts"))
                        .supplier(ExprActiveRotation::new)
                        .build()
        );
    }

    @Override
    public Quaternionf convert(Active from) {
        Quaternionf q;
        if (from instanceof ActivePart p && p.isDisplay()){
            q = p.getTransformation().getLeftRotation();
        }
        else if (from instanceof ActiveGroup<?> g){
            q = g.getRotation();
        }
        else{
            q = null;
        }
        return q;
    }

    @Override
    protected String getPropertyName() {
        return "deu rotation";
    }

    @Override
    public Class<? extends Quaternionf> getReturnType() {
        return Quaternionf.class;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Object[] active = getExpr().getArray(event);
        if (active == null){
            return;
        }

        switch (mode) {
            case SET -> {
                if (delta == null) return;

                Object o =  delta[0];
                if (o == null) return;

                Quaternionf q;
                Number n;
                switch (o) {
                    case Quaternionf quaternionf -> {
                        q = quaternionf;
                        n = null;
                    }
                    case Vector vector -> {
                        q = new Quaternionf().rotationXYZ(
                                (float) Math.toRadians(vector.getX()),
                                (float) Math.toRadians(vector.getY()),
                                (float) Math.toRadians(vector.getZ())
                        );
                        n = null;
                    }
                    case Number number -> {
                        q = null;
                        n = number;
                    }
                    default -> {
                        return;
                    }
                }

                if (q != null && !q.isFinite()) return;

                for (Object a : active){
                    if (a instanceof ActiveGroup<?> g){
                        if (axis == null){
                            if (q == null) return;
                            g.setRotation(q, true);
                        }
                        else{
                            if (n == null) return;
                            g.setRotation(n.floatValue(), axis, true);
                        }
                    }
                    else if (a instanceof ActivePart p){
                        if (axis == null){
                            if (q == null) return;
                            p.setRotation(q);
                        }
                        else{
                            if (n == null) return;
                            p.setRotation(n.floatValue(), axis);
                        }
                    }
                }
            }
            case RESET -> {
                for (Object a : active){
                    if (a instanceof ActiveGroup<?> g){
                        if (axis == null) g.resetRotation(true);
                        else g.setRotation(0, axis, true);
                    }
                    else if (a instanceof ActivePart part){
                        if (axis == null) part.setRotation(new Quaternionf());
                        else part.setRotation(0, axis);
                    }
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Quaternionf.class, Vector.class, Number.class);
        }
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (parseResult.hasTag("x")) axis = Axis.X;
        else if (parseResult.hasTag("y")) axis = Axis.Y;
        else if (parseResult.hasTag("z")) axis = Axis.Z;
        else axis = null;
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }
}
