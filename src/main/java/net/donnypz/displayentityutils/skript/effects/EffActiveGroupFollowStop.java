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
        Skript.registerEffect(EffActiveGroupFollowStop.class,"make %activegroups% stop follow[ing] [entit(y|ies)]");
    }

    Expression<ActiveGroup<?>> groups;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        groups = (Expression<ActiveGroup<?>>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup<?>[] gs = groups.getArray(event);
        if (gs == null){
            return;
        }
        for (ActiveGroup<?> g : gs){
            g.stopFollowingEntity();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return groups.toString(event, debug)+" stop following entity";
    }
}
