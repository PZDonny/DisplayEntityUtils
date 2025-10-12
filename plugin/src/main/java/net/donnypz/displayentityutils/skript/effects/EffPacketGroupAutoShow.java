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
            "make {_packetgroup} stop autoshowing",
            "",
            "3.3.4+",
            "make {_packetgroup} stop autoshowing and hide from current viewers"})
@Since("3.2.0, 3.3.4 (Hide)")
public class EffPacketGroupAutoShow extends Effect {
    static {
        Skript.registerEffect(EffPacketGroupAutoShow.class,"make %packetgroups% [:stop] autoshow[ing] [h:and hide from current [players|viewers]]");
    }

    Expression<PacketDisplayEntityGroup> group;
    boolean autoshow;
    boolean hide;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<PacketDisplayEntityGroup>) expressions[0];
        autoshow = !parseResult.hasTag("stop");
        hide = parseResult.hasTag("h");
        return true;
    }

    @Override
    protected void execute(Event event) {
        PacketDisplayEntityGroup[] groups = group.getArray(event);
        if (groups == null) return;
        for (PacketDisplayEntityGroup g : groups){
            if (g != null){
                g.setAutoShow(autoshow);
                if (!autoshow && hide){
                    g.hide();
                }
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "packet group autoshow: "+ group.toString(event, debug);
    }
}
