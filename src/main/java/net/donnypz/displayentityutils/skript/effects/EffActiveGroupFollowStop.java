package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Stop Follow Entity Direction")
@Description("Make an active group stop following an entity's looking direction")
@Examples({"make {_spawnedgroup} stop following entity"})
@Since("3.2.1")
public class EffActiveGroupFollowStop extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupFollowStop.class,"make %activegroup% stop follow[ing] [entit(y|ies)]");
    }

    Expression<ActiveGroup<?>> group;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup<?>>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup<?> g = group.getSingle(event);
        if (g == null){
            return;
        }
        g.stopFollowingEntity();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return group.toString(event, debug)+" stop following entity";
    }
}
