package net.donnypz.displayentityutils.skript.group.activegroup.effects;

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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group Stop Respecting Entity")
@Description("Make an active group stop respecting an entity's looking direction")
@Examples({"make {_spawnedgroup} stop following entity",
            "make {_activegroup} stop following",
            "",
            "#3.4.3+",
            "make {_activegroup} stop respecting entity",
            "make {_activegroup} end respect"})
@Since({"3.2.1"})
public class EffActiveGroupRespectStop extends Effect {

    Expression<ActiveGroup<?>> groups;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupRespectStop.class)
                        .addPattern("make %activegroups% (stop|end) (follow|respect)[ing] [entit(y|ies)]")
                        .supplier(EffActiveGroupRespectStop::new)
                        .build()
        );
    }

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
        return groups.toString(event, debug)+" stop respecting entity";
    }
}
