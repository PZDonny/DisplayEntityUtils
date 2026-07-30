package net.donnypz.displayentityutils.skript.misc.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Valid Tag")
@Description("Check if a tag is valid and can be applied to any deu object that uses tags")
@Examples({"if \"my_special_tag\" is a valid deu tag:", "\tbroadcast \"The tag is valid!\""})
@Since("3.6.0")
public class CondValidTag extends Condition {

    Expression<String> tagExpr;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondValidTag.class)
                        .addPattern("%string% (1¦is|2¦is(n't| not)) [a] valid deu tag")
                        .supplier(CondValidTag::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        String tag = tagExpr.getSingle(event);
        if (tag == null) return isNegated();
        return DisplayUtils.isValidTag(tag) != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Valid Tag: "+ tagExpr.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        tagExpr = (Expression<String>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
