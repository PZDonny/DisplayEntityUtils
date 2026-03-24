package net.donnypz.displayentityutils.skript.parts.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Is Part of Display Type?")
@Description("Check if an active part is of a display entity type (block, item, text)")
@Examples({"if {_activepart} is a display part:",
        "\tbroadcast \"This part is a block, item, or text display!\""})
@Since("3.5.0")
public class CondPartIsDisplayPart extends Condition {

    Expression<ActivePart> partExpr;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondPartIsDisplayPart.class)
                        .addPattern("%activepart% (1¦is|2¦is(n't| not)) a display [entity] part")
                        .supplier(CondPartIsDisplayPart::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        ActivePart part = partExpr.getSingle(event);

        return part != null ? part.isDisplay() : isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Is Part of Display Type: "+ partExpr.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.partExpr = (Expression<ActivePart>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
