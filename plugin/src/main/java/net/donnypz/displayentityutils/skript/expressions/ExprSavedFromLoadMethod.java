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
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Get Group/Animation From Storage")
@Description("Get a saved group/animation from a load method (local, mongodb, mysql)")
@Examples({"set {_savedanim} to animation tagged \"myanimation\" from mysql storage",
        "set {_savedgroup} to saved group tagged \"mygroup\" from local storage"})
@Since("3.3.1")
public class ExprSavedFromLoadMethod extends SimpleExpression<Object> {

    static{
        Skript.registerExpression(ExprSavedFromLoadMethod.class, Object.class, ExpressionType.SIMPLE, "(g:saved group|anim[ation]) [tagged] %string% from (1¦local|2¦mysql|3¦mongo[db]) [storage]");
    }

    private Expression<String> tag;
    private LoadMethod loadMethod;
    private boolean isGroup;

    @Override
    protected Object @Nullable [] get(Event event) {
        String t = tag.getSingle(event);
        if (t == null || loadMethod == null){
            return null;
        }

        if (isGroup){
            return new DisplayEntityGroup[]{DisplayGroupManager.getGroup(loadMethod, t)};
        }
        else{
            return new SpawnedDisplayAnimation[]{DisplayAnimationManager.getSpawnedDisplayAnimation(t, loadMethod)};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (isGroup ? "saved group" : "animation") + " tagged " + tag.toString(event, debug) + " from " + loadMethod.name().toLowerCase();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        tag = (Expression<String>) expressions[0];
        isGroup = parseResult.hasTag("g");
        switch (parseResult.mark){
            case 1 -> loadMethod = LoadMethod.LOCAL;
            case 2 -> loadMethod = LoadMethod.MYSQL;
            case 3 -> loadMethod = LoadMethod.MONGODB;
        }
        return true;
    }
}
