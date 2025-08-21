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

@Name("Packet Group Autoshow")
@Description("Determine if a Packet Group should automatically reveal itself to players")
@Examples({"make {_packetgroup} autoshow",
            "make {_packetgroup} stop autoshowing"})
@Since("3.2.0")
public class EffPacketGroupAutoShow extends Effect {
    static {
        Skript.registerEffect(EffPacketGroupAutoShow.class,"make %packetgroups% [:stop] autoshow[ing]");
    }

    Expression<PacketDisplayEntityGroup> group;
    boolean autoshow;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<PacketDisplayEntityGroup>) expressions[0];
        autoshow = !parseResult.hasTag("stop");
        return true;
    }

    @Override
    protected void execute(Event event) {
        PacketDisplayEntityGroup[] groups = group.getArray(event);
        if (groups == null) return;
        for (PacketDisplayEntityGroup g : groups){
            if (g != null){
                g.setAutoShow(autoshow);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "packet group autoshow: "+ group.toString(event, debug);
    }
}
