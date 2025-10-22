package net.donnypz.displayentityutils.skript.conditions;

import ch.njol.skript.Skript;
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

@Name("ActivePart / Display Is Master Part?")
@Description("Check if an active part or a display entity is the master part of an active group")
@Examples({"if {_spawnedpart} is the master part:", "\tbroadcast \"All other parts are the passengers of this one!\""})
@Since("2.6.2")
public class CondIsMaster extends Condition {

    static {
        Skript.registerCondition(CondIsMaster.class, "%activepart/display% (1¦is|2¦is(n't| not)) [the] master part [of a [spawned|packet] group]");
    }

    Expression<?> object;

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
