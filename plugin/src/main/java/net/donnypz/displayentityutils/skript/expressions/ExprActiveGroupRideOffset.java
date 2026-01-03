package net.donnypz.displayentityutils.skript.expressions;

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

@Name("Active Group Ride Offset")
@Description("Get or set the ride offset to apply to a group. This should be used before making the group ride any entities")
@Examples({"set {_offsetvector} to {_activegroup}'s ride offset",
            "",
            "set {_activegroup}'s ride offset to vector(0,1,0)"})
@Since("3.4.1")
public class ExprActiveGroupRideOffset extends SimplePropertyExpression<ActiveGroup, Vector> {
    static {
        register(ExprActiveGroupRideOffset.class, Vector.class, "[deu] (rid(ing|e)|mount) [translation] offset", "activegroups");
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
        return "ride offset";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        ActiveGroup group = getExpr().getSingle(event);

        if (group == null){
            return;
        }
        switch (mode) {
            case SET -> {
                if (delta == null){
                    return;
                }
                Vector offset = (Vector) delta[0];
                group.setRideOffset(offset);
            }
            case RESET -> {
                group.setRideOffset(new Vector());
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Vector.class);
        }
        return null;
    }
}
