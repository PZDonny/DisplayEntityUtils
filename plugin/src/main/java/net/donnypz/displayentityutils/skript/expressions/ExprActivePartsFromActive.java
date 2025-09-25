package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Name("Active Parts of Group / Part Selection")
@Description("Get the active/packet parts of a group or part selection")
@Examples({"set {_spawnedparts::*} to {_spawnedgroup}'s parts",
        "",
        "#3.0.0 and later",
        "set {_packetparts::*} to {_packetpartselection}'s parts"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartsFromActive extends PropertyExpression<Object, ActivePart> {

    static {
        register(ExprActivePartsFromActive.class, ActivePart.class, "[active] parts", "activegroups/multipartselections");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr(expressions[0]);
        return true;
    }

    @Override
    public Class<? extends ActivePart> getReturnType() {
        return ActivePart.class;
    }

    @Override
    protected ActivePart[] get(Event event, Object[] objects) {
        return Arrays.stream(objects)
                .flatMap(object -> {
                    List<? extends ActivePart> parts = null;
                    if (object instanceof SpawnedDisplayEntityGroup g)
                        parts = g.getParts();
                    else if (object instanceof SpawnedPartSelection sel)
                        parts = sel.getSelectedParts();
                    else if (object instanceof PacketDisplayEntityGroup g)
                        parts = g.getParts();
                    else if (object instanceof PacketPartSelection sel)
                        parts = sel.getSelectedParts();
                    return parts == null ? Stream.empty() : parts.stream();
                })
                .filter(Objects::nonNull)
                .toArray(ActivePart[]::new);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "active parts of " + getExpr().toString(event, b);
    }
}
