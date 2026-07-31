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
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Part - Has Part Tag")
@Description("Check if an active part entity has a part tag")
@Examples({"if {_activepart} has part tag \"head\":",
        "\tbroadcast \"The part has the \"head\" part tag!\""})
@Since("3.6.0")
public class CondPartHasTag extends Condition {

    Expression<?> partExpr;
    Expression<String> tagExpr;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondPartHasTag.class)
                        .addPattern("%activepart/entity% (1¦has|2¦(has no|does(n't| not) have)) part tag %string%")
                        .supplier(CondPartHasTag::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        Object obj = partExpr.getSingle(event);
        String tag = tagExpr.getSingle(event);
        if (tag == null) return isNegated();

        if (obj instanceof ActivePart p){
            return p.hasTag(tag) != isNegated();
        }
        else if (obj instanceof Entity e){
            return DisplayUtils.hasPartTag(e, tag) != isNegated();
        }
        return isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return partExpr.toString(event, debug)+" has part tag: "+tagExpr.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.partExpr = expressions[0];
        this.tagExpr = (Expression<String>) expressions[1];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
