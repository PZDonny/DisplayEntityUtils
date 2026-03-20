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

@Name("Dismount Active Group")
@Description("Make an active group stop riding an entity")
@Examples({"deu dismount {_activegroup}"})
@Since("2.6.2, 3.0.0 (Packet)")
public class EffActiveGroupDismount extends Effect {

    Expression<ActiveGroup> group;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupDismount.class)
                        .addPattern("deu dismount %activegroup%")
                        .supplier(EffActiveGroupDismount::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null){
            return;
        }
        g.dismount();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "dismount "+group.toString(event, debug);
    }
}
