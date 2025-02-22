package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Saved Animation from Cache")
@Description("Get a spawned animation that has been cached")
@Examples({"set {_spawnedanim} to cached spawned anim tagged \"myanimation\"",
"set {_spawnedanim} to cached spawned anim tagged \"myotheranim\" or fallback with local storage"})
@Since("2.6.2")
public class ExprSpawnedAnimFromCache extends SimpleExpression<SpawnedDisplayAnimation> {

    static{
        Skript.registerExpression(ExprSpawnedAnimFromCache.class, SpawnedDisplayAnimation.class, ExpressionType.SIMPLE, "cached spawned[ |-]anim[ation] [tagged] %string% [o:or [fallback] (with|from) (1¦local|2¦mysql|3¦mongo[db]) [storage]]");
    }

    Expression<String> tag;
    LoadMethod loadMethod;
    boolean tryLoadMethod;

    @Override
    protected SpawnedDisplayAnimation @Nullable [] get(Event event) {
        String t = tag.getSingle(event);
        if (t == null){
            return null;
        }
        if (tryLoadMethod){
            if (loadMethod == null){
                return null;
            }
            return new SpawnedDisplayAnimation[]{DisplayAnimationManager.getSpawnedDisplayAnimation(t, loadMethod)};
        }
        else{
            return new SpawnedDisplayAnimation[]{DisplayAnimationManager.getCachedAnimation(t)};
        }


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

        return "cached spawned animation, tagged: "+tag.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        tag = (Expression<String>) expressions[0];
        if (parseResult.hasTag("o")){
            tryLoadMethod = true;
            switch (parseResult.mark){
                case 1 -> loadMethod = LoadMethod.LOCAL;
                case 2 -> loadMethod = LoadMethod.MYSQL;
                case 3 -> loadMethod = LoadMethod.MONGODB;
            }
        }

        return true;
    }
}
