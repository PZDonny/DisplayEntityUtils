package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name("Active Part's Tags")
@Description("Get all the tags of an active part")
@Examples({"set {_tags::*} to {_spawnedpart}'s part tags"})
@Since("2.6.2")
public class ExprSpawnedPartTags extends PropertyExpression<ActivePart, String> {

    static {
        register(ExprSpawnedPartTags.class, String.class, "active part tags", "activeparts");
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
