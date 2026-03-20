package net.donnypz.displayentityutils.skript.group.activegroup.expressions;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Save Active Group")
@Description("Get an unmodifiable version of an Active Group")
@Examples({"set {_savedgroup} to {_activegroup} as saved group"})
@Since("3.3.1")
public class ExprActiveGroupToSaved extends SimpleExpression<DisplayEntityGroup> {

    private Expression<?> object;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveGroupToSaved.class, DisplayEntityGroup.class)
                        .addPatterns("%activegroup% as saved[ |-]group")
                        .supplier(ExprActiveGroupToSaved::new)
                        .build()
        );
    }


    @Override
    protected DisplayEntityGroup[] get(Event event) {
        Object o = object.getSingle(event);
        if (o instanceof ActiveGroup<?> ag){
            return new DisplayEntityGroup[]{ag.toDisplayEntityGroup()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<DisplayEntityGroup> getReturnType() {
        return DisplayEntityGroup.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return object.toString(event,debug)+" to saved group";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        return true;
    }
}
