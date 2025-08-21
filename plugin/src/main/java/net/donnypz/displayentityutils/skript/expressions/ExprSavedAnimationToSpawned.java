package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Saved Animation to Spawned Animation")
@Description("Get a spawned animation that can be used on active groups")
@Examples({"set {_spawnedanim} to {_savedanim} as spawned animation"})
@Since("2.6.2")
public class ExprSavedAnimationToSpawned extends SimpleExpression<SpawnedDisplayAnimation> {

    static{
        Skript.registerExpression(ExprSavedAnimationToSpawned.class, SpawnedDisplayAnimation.class, ExpressionType.SIMPLE, "%savedanimation% as spawned[ |-][anim[ation]]");
    }

    Expression<DisplayAnimation> savedAnim;

    @Override
    protected SpawnedDisplayAnimation @Nullable [] get(Event event) {
        DisplayAnimation saved = savedAnim.getSingle(event);
        if (saved == null){
            return null;
        }
        return new SpawnedDisplayAnimation[]{saved.toSpawnedDisplayAnimation()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends SpawnedDisplayAnimation> getReturnType() {
        return SpawnedDisplayAnimation.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return savedAnim.toString(event,debug)+" to spawned animation";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        savedAnim = (Expression<DisplayAnimation>) expressions[0];
        return true;
    }
}
