package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned/Packet Parts of Group / Part Selection")
@Description("Get the active/packet parts of a group or part selection")
@Examples({"set {_spawnedparts::*} to {_spawnedgroup}'s parts",
        "",
        "#3.0.0 and later",
        "set {_packetparts::*} to {_packetpartselection}'s parts"})
@Since("2.6.2")
public class ExprActivePartsFromActive extends SimpleExpression<ActivePart> {

    static {
        String property = "[the] parts";
        String fromType = "activegroup/multipartselection";
        Skript.registerExpression(ExprActivePartsFromActive.class, ActivePart.class, ExpressionType.PROPERTY, PropertyExpression.getPatterns(property, fromType));
    }

    Expression<Object> active;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        active = (Expression<Object>) expressions[0];
        return true;
    }

    @Override
    public Class<? extends ActivePart> getReturnType() {
        return ActivePart.class;
    }


    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    protected ActivePart @Nullable [] get(Event event) {
        Object obj = active.getSingle(event);
        if (obj instanceof SpawnedDisplayEntityGroup g){
            return g.getParts().toArray(new SpawnedDisplayEntityPart[0]);
        }
        else if (obj instanceof SpawnedPartSelection sel){
            return sel.getSelectedParts().toArray(new SpawnedDisplayEntityPart[0]);
        }
        if (obj instanceof PacketDisplayEntityGroup g){
            return g.getParts().toArray(new PacketDisplayEntityPart[0]);
        }
        else if (obj instanceof PacketPartSelection sel){
            return sel.getSelectedParts().toArray(new PacketDisplayEntityPart[0]);
        }

        return null;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "parts from" + active.toString(event, debug);
    }
}
