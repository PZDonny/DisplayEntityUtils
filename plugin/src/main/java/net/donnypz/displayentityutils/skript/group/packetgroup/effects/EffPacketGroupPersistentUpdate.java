package net.donnypz.displayentityutils.skript.group.packetgroup.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.skript.group.activegroup.effects.EffActiveGroupPersistence;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Update Persistent Packet Group")
@Description("Confirm any changes made to a persistent packet group, making them persist in future game sessions. \nThis does nothing if the packet group is not persistent.")
@Examples({"update persistent packet group {_packetgroup}"})
@Since("3.3.4, 3.5.0 (confirm)")
public class EffPacketGroupPersistentUpdate extends Effect {

    Expression<PacketDisplayEntityGroup> packetGroup;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupPersistence.class)
                        .addPattern("(update|confirm) persistent [packet group] %packetgroups%")
                        .supplier(EffActiveGroupPersistence::new)
                        .build()
        );
    }

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