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
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Is Part Master/Parent?")
@Description("Check if an active part or a display entity is the master/parent part of an active group")
@Examples({"if {_spawnedpart} is the master part:",
        "\tbroadcast \"All other parts are the passengers of this one!\""})
@Since("2.6.2, 3.5.0 (parent)")
public class CondPartIsMaster extends Condition {

    Expression<?> object;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondPartIsMaster.class)
                        .addPattern("%activepart/display% (1¦is|2¦is(n't| not)) [the] (master|parent) part [of a [active] group]")
                        .supplier(CondPartIsMaster::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        Object obj = object.getSingle(event);
        Display entity;
        switch (obj) {
            case ActivePart p -> {
                return p.isMaster() != isNegated();
            }
            case Display display -> entity = display;
            case null, default -> {
                return isNegated();
            }
        }
        return DisplayUtils.isMaster(entity) != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Master part: "+object.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.object = expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
