package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;

@Name("Active Part's Tags")
@Description("Get all the tags of a part")
@Examples({"set {_tags::*} to {_activepart}'s part tags"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartTags extends PropertyExpression<ActivePart, String> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActivePartTags.class, String.class)
                        .addPatterns(getPatterns("[active] part tags", "activeparts"))
                        .supplier(ExprActivePartTags::new)
                        .build()
        );
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String[] get(Event event, ActivePart[] source) {
        return Arrays.stream(source).flatMap(activePart -> activePart.getTags().stream()).toArray(String[]::new);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "active part tags of "+ getExpr().toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends ActivePart>) expressions[0]);
        return true;
    }
}
