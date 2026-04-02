package net.donnypz.displayentityutils.skript.group.activegroup.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group Ride Offset")
@Description("Get or set the translation offset to apply to a group when its riding an entity. " +
        "This should be used before making a group ride any entities")
@Examples({
        "#This should be set BEFORE a group rides an entity",
        "set {_offsetvector} to {_activegroup}'s deu ride offset",
        "",
        "set {_activegroup}'s deu ride offset to vector(0,1,0)"
})
@Since("3.4.1")
public class ExprActiveGroupRideOffset extends SimplePropertyExpression<ActiveGroup, Vector> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveGroupRideOffset.class, Vector.class)
                        .addPatterns(getPatterns("deu (rid(ing|e)|mount) [translation] offset", "activegroups"))
                        .supplier(ExprActiveGroupRideOffset::new)
                        .build()
        );
    }

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    @Nullable
    public Vector convert(ActiveGroup group) {
        return group.getRideOffset();
    }

    @Override
    protected String getPropertyName() {
        return "deu ride offset";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        ActiveGroup[] groups = getExpr().getArray(event);

        if (groups == null){
            return;
        }
        switch (mode) {
            case SET -> {
                if (delta != null){
                    Vector offset = (Vector) delta[0];
                    for (ActiveGroup group : groups){
                        group.setRideOffset(offset);
                    }
                }
            }
            case RESET -> {
                for (ActiveGroup group: groups){
                    group.setRideOffset(new Vector());
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Vector.class);
        }
        return null;
    }
}
