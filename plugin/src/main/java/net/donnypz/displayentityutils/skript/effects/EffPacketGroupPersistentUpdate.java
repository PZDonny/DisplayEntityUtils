package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Update Persistent Packet Group")
@Description("Update a persistent packet group to make any changes applied to it apply future sessions. This does nothing if the packet group is not persistent.")
@Examples({"update persistent packet group {_packetgroup}",
        "update persistent {_packetgroup}"})
@Since("3.3.4")
public class EffPacketGroupPersistentUpdate extends Effect {
    static {
        Skript.registerEffect(net.donnypz.displayentityutils.skript.effects.EffActiveGroupPersistence.class,"update persistent [packet group] %packetgroups%");
    }

    Expression<PacketDisplayEntityGroup> packetGroup;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        packetGroup = (Expression<PacketDisplayEntityGroup>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        PacketDisplayEntityGroup[] groups = packetGroup.getArray(event);
        if (groups == null) return;
        for (PacketDisplayEntityGroup g : groups){
            if (g != null && g.isPersistent()){
                g.update();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "update persistent packet group: "+ packetGroup.toString(event, debug);
    }
}