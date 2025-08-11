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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Mount/Ride")
@Description("Make an active group ride an entity or vice versa")
@Examples({
        "#Use \"mount\" instead of \"ride\" if you experience unexpected behavior",
        "",
        "#Spawned/Packet Group Ride Entity",
        "deu make {_packetgroup} ride {_entity}",
        "deu make {_spawnedgroup} mount {_entity} using controller with id \"mycontroller\"",
        "",
        "#Entity Ride Spawned Group",
        "deu make {_entity} mount {_spawnedgroup}",
        "deu make {_entity} ride {_spawnedgroup} using controller \"mycontroller2\""})
@Since("2.6.2")
public class EffActiveGroupRideEntity extends Effect {

    static {
        Skript.registerEffect(EffActiveGroupRideEntity.class,"[deu] make %activegroup/entity% (mount|ride) %activegroup/entity% [c:using controller [with id] %-string%]");
    }

    Expression<?> obj1;
    Expression<?> obj2;
    Expression<String> controllerID;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        obj1 = expressions[0];
        obj2 = expressions[1];

        if (parseResult.hasTag("c")){
            controllerID = (Expression<String>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object o1 = obj1.getSingle(event);
        Object o2 = obj2.getSingle(event);
        if (o1 == null || o2 == null){
            return;
        }

        if (o1.getClass() == o2.getClass()){
            return;
        }

        ActiveGroup<?> g;
        Entity e;

        boolean rideEntity = o1 instanceof ActiveGroup<?>;

        //Group Ride Entity
        if (rideEntity){
            g = (ActiveGroup<?>) o1;
            e = (Entity) o2;
            boolean applied = applyController(event, g, e);
            g.rideEntity(e);
            if (applied) DisplayControllerManager.registerEntity(e, g);
        }

        //Entity Ride Group
        else if (o2 instanceof SpawnedDisplayEntityGroup sg){
            e = (Entity) o1;
            g = sg;
            SpawnedDisplayEntityPart masterPart = sg.getMasterPart();
            if (masterPart == null){
                return;
            }
            Entity masterEntity = masterPart.getEntity();
            if (masterEntity == null){
                return;
            }
            applyController(event, g, e);
            masterEntity.addPassenger(e);
        }
    }

    private boolean applyController(Event event, ActiveGroup<?> g, Entity e){
        if (controllerID != null){
            DisplayController controller = DisplayController.getController(controllerID.getSingle(event));
            if (controller == null){
                return false;
            }
            controller.apply(e, g, false);
            return true;
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return obj1.toString(event, debug)+" ride "+obj2.toString(event, debug);
    }
}
