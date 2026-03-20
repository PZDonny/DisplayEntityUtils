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

@Name("Text Display Part See Through Blocks")
@Description("Set whether a text display part/entity should be visible through walls")
@Examples({
        "deu make {_activepart} visible through blocks",
        "deu stop {_activepart} from being seen through walls"})
@Since("3.5.0")
public class EffTextDisplaySeeThrough extends Effect {

    Expression<ActivePart> partExpr;
    boolean hide;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffTextDisplaySeeThrough.class)
                        .addPattern("deu make %activeparts% visible through (blocks|walls)")
                        .addPattern("deu (stop|prevent|block) %activeparts% from being (visible|seen) through (blocks|walls)")
                        .supplier(EffTextDisplaySeeThrough::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        partExpr = (Expression<ActivePart>) expressions[0];
        hide = matchedPattern == 1;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActivePart[] parts = partExpr.getArray(event);
        if (parts == null){
            return;
        }

        for (ActivePart part : parts){
            part.setTextDisplaySeeThrough(!hide);
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (!hide)
            return "deu force " + partExpr.toString(event, debug) + " to be visible through blocks";
        return "deu prevent " + partExpr.toString(event, debug) + " from being visible through blocks";
    }
}
