package net.donnypz.displayentityutils.skript.parts.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Interaction Part's Responsiveness")
@Description("Set whether a interaction part should be visible through walls")
@Examples({
        "deu make {_activepart} responsive",
        "deu stop {_activepart} from being responsive"})
@Since("3.5.0,  3.5.2 (Interaction Entities)")
public class EffInteractionResponsive extends Effect {

    Expression<?> partExpr;
    boolean negate;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffInteractionResponsive.class)
                        .addPattern("deu make %activeparts% [interaction] responsive")
                        .addPattern("deu (stop|prevent|block) %activeparts/entities% from being [interaction] responsive")
                        .supplier(EffInteractionResponsive::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        partExpr = expressions[0];
        negate = matchedPattern == 1;
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object[] parts = partExpr.getArray(event);
        if (parts == null){
            return;
        }

        for (Object obj : parts){
            if (obj instanceof ActivePart part){
                part.setInteractionResponsive(!negate);
            }
            else if (obj instanceof Interaction i){
                i.setResponsive(!negate);
            }
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (!negate)
            return "deu force " + partExpr.toString(event, debug) + " to be responsive";
        return "deu prevent " + partExpr.toString(event, debug) + " from being responsive";
    }
}
