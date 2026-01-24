package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Persistent Packet Group From Id")
@Description("Get a Persistent Packet Group from its ID")
@Examples({
        "#Save the persistent packet group's ID",
        "set {id} to {_packetgroupA}'s persistent id",
        "",
        "#Use the {id} to get the persistent packet group at any time",
        "set {_packetgroupB} to persistent packet group with id {id}"
})
@Since("3.3.4")
public class ExprPersistentPacketGroupFromId extends SimpleExpression<PacketDisplayEntityGroup> {

    static{
        Skript.registerExpression(ExprPersistentPacketGroupFromId.class, PacketDisplayEntityGroup.class, ExpressionType.SIMPLE, "persistent packet group (from|with) [global] id %string%");
    }

    private Expression<String> id;

    @Override
    protected PacketDisplayEntityGroup[] get(Event event) {
        String id = this.id.getSingle(event);
        if (id == null){
            return null;
        }
        PacketDisplayEntityGroup pdeg = PacketDisplayEntityGroup.getGroup(id);
        if (pdeg == null) return new PacketDisplayEntityGroup[0];
        return new PacketDisplayEntityGroup[]{pdeg};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends PacketDisplayEntityGroup> getReturnType() {
        return PacketDisplayEntityGroup.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "persistent packet group from id \""+id.toString(event, debug)+"\"";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        return true;
    }
}
