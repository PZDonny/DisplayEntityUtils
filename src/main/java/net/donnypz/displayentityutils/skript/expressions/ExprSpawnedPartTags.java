package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Part's Tags")
@Description("Get all the tags of a spawned part")
@Examples({"set {_tags::*} to {_spawnedpart}'s part tags"})
@Since("2.6.2")
public class ExprSpawnedPartTags extends SimpleExpression<String> {

    static {
        String property = "[the] [part] tags";
        String fromType = "spawnedpart";
        Skript.registerExpression(ExprSpawnedPartTags.class, String.class, ExpressionType.PROPERTY, PropertyExpression.getPatterns(property, fromType));
    }

    Expression<SpawnedDisplayEntityPart> spawnedPart;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }


    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    protected String @Nullable [] get(Event event) {
        SpawnedDisplayEntityPart part = spawnedPart.getSingle(event);
        if (part == null){
            return null;
        }
        return part.getTags().toArray(new String[0]);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "part tags from "+spawnedPart.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        spawnedPart = (Expression<SpawnedDisplayEntityPart>) expressions[0];
        return true;
    }
}
