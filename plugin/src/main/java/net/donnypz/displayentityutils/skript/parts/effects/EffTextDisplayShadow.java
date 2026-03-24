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
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Text Display Part Shadow")
@Description("Set whether a text display part's text should have a shadow")
@Examples({
        "deu add text shadow to {_activepart}'s text",
        "deu remove text shadow from {_activepart}'s text"})
@Since("3.5.0")
public class EffTextDisplayShadow extends Effect {

    Expression<ActivePart> partExpr;
    boolean remove;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffTextDisplayShadow.class)
                        .addPattern("deu (apply|add) (drop|text) shadow to %activeparts%['s] text")
                        .addPattern("deu (remove|clear) (drop|text) shadow from %activeparts%['s] text")
                        .supplier(EffTextDisplayShadow::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        partExpr = (Expression<ActivePart>) expressions[0];
        remove = matchedPattern == 1;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActivePart[] parts = partExpr.getArray(event);
        if (parts == null){
            return;
        }

        for (ActivePart part : parts){
            part.setTextDisplayShadowed(!remove);
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (!remove)
            return "deu add drop shadow to " + partExpr.toString(event, debug);
        return "deu remove drop shadow from " + partExpr.toString(event, debug);
    }
}
