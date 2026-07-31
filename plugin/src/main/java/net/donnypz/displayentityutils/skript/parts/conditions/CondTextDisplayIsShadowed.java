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

@Name("Text Display Part - Has Shadows")
@Description("Check if an text display part's text has shadows")
@Examples({"if {_activepart} has deu text shadows:",
        "\tbroadcast \"This text display part has text shadow!\""})
@Since("3.5.0, 3.5.2 (Text Displays Entities)")
public class CondTextDisplayIsShadowed extends Condition {

    Expression<?> partExpr;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondTextDisplayIsShadowed.class)
                        .addPattern("%activepart/display% (1¦(is|has)|2¦(has no|does(n't| not) have)) deu [text] [drop] shadow[ed|s]")
                        .supplier(CondTextDisplayIsShadowed::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        Object obj = partExpr.getSingle(event);
        if (obj instanceof ActivePart p){
            return p.isTextDisplayShadowed() != isNegated();
        }
        else if (obj instanceof TextDisplay td){
            return td.isShadowed() != isNegated();
        }
        return isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "text display part has shadows: "+ partExpr.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.partExpr = expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
