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
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;


@Name("Billboard")
@Description("Get/Set the billboard of an active group/part/filter")
@Examples({
        "set {_activepart}'s deu billboard to vertical",
        "set {_activegroup}'s deu billboard to center",
        "",
        "set {_billboard} to {_activepart}'s billboard",
        "",
        "#3.4.3 and earlier",
        "set {_activepart}'s billboard to vertical",
        "set {_activegroup}'s billboard to center",
})
@Since("2.6.2, 3.0.0 (Packet Types), 3.5.0 (Plural)")
public class ExprActiveBillboard extends SimplePropertyExpression<Active, Display.Billboard> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveBillboard.class, Display.Billboard.class)
                        .addPatterns(getPatterns("deu billboard", "activegroups/activeparts/partfilters"))
                        .supplier(ExprActiveBillboard::new)
                        .build()
        );
    }

    @Override
    public Display.Billboard convert(Active from) {
        if (from instanceof ActivePart p){
            return p.getBillboard();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "deu billboard";
    }

    @Override
    public Class<? extends Display.Billboard> getReturnType() {
        return Display.Billboard.class;
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

                Display.Billboard b = (Display.Billboard) delta[0];
                if (b == null) return;

                for (Active a : getExpr().getArray(event)){
                    a.setBillboard(b);
                }
            }
            case RESET -> {
                for (Active a : getExpr().getArray(event)){
                    a.setBillboard(Display.Billboard.FIXED);
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Display.Billboard.class);
        }
        return null;
    }
}
