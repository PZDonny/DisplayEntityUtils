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
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Saved Group to Packet Group")
@Description("Create a packet based group from a saved group")
@Examples({"set {_packetgroup} to packet based {_savedgroup} spawned at {_location}",
        "",
        "#(3.3.4+) Spawn packet based group that will persist after restarts",
        "set {_packetgroup} to persistent packet based {_savedgroup} spawned at {_location}"})
@Since("3.0.0, 3.3.4 (Persistent)")
public class ExprSavedGroupToPacket extends SimpleExpression<PacketDisplayEntityGroup> {

    static{
        Skript.registerExpression(ExprSavedGroupToPacket.class, PacketDisplayEntityGroup.class, ExpressionType.COMBINED, "[:persistent] packet [based] %savedgroup% spawned at %location%");
    }

    private Expression<DisplayEntityGroup> savedGroup;
    private Expression<Location> location;
    boolean persistent;

    @Override
    protected PacketDisplayEntityGroup @Nullable [] get(Event event) {
        DisplayEntityGroup saved = savedGroup.getSingle(event);
        if (saved == null) return new PacketDisplayEntityGroup[0];
        Location loc = location.getSingle(event);
        if (loc == null) return new PacketDisplayEntityGroup[0];
        if (persistent){
            return new PacketDisplayEntityGroup[]{DisplayGroupManager.addPersistentPacketGroup(loc, saved, false)};
        }
        else{
            return new PacketDisplayEntityGroup[]{saved.createPacketGroup(loc, true)};
        }
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
        return (persistent ? "persistent" : "")+"packet based " + savedGroup.toString(event, debug)+" spawned at " + location.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        savedGroup = (Expression<DisplayEntityGroup>) expressions[0];
        location = (Expression<Location>) expressions[1];
        persistent = parseResult.hasTag("persistent");
        return true;
    }
}
