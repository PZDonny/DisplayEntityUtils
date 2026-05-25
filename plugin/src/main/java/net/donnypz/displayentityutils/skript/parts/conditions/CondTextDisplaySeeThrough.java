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
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Text Display Part Visible Through Blocks?")
@Description("Check if an text display part is visible through blocks")
@Examples({"if {_activepart} is deu visible through blocks:",
        "\tbroadcast \"This text display part is visible through walls!\""})
@Since("3.5.0, 3.5.2 (Text Displays Entities)")
public class CondTextDisplaySeeThrough extends Condition {

    Expression<?> partExpr;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondTextDisplaySeeThrough.class)
                        .addPattern("%activepart/displays% (1¦is|2¦is(n't| not)) deu visible through (blocks|walls)")
                        .supplier(CondTextDisplaySeeThrough::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        Object obj = partExpr.getSingle(event);
        if (obj instanceof ActivePart p){
            return p.isTextDisplaySeeThrough();
        }
        else if (obj instanceof TextDisplay td){
            return td.isSeeThrough();
        }
        return isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "text display part visible through blocks: "+ partExpr.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.partExpr = expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
