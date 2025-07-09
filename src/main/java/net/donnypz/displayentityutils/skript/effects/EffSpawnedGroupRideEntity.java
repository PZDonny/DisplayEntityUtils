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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group Mount/Ride")
@Description("Make a spawned group ride an entity or vice versa")
@Examples({
        "#Use \"mount\" instead of \"ride\" if you experience unexpected behavior",
        "",
        "#Spawned Group Ride Entity",
        "deu make {_spawnedgroup} ride {_entity}",
        "deu make {_spawnedgroup} mount {_entity} using controller with id \"mycontroller\"",
        "",
        "#Entity Ride Spawned Group",
        "deu make {_entity} mount {_spawnedgroup}",
        "deu make {_entity} ride {_spawnedgroup} using controller \"mycontroller2\""})
@Since("2.6.2")
public class EffSpawnedGroupRideEntity extends Effect {

    static {
        Skript.registerEffect(EffSpawnedGroupRideEntity.class,"[deu] make %spawnedgroup/entity% (mount|ride) %spawnedgroup/entity% [c:using controller [with id] %-string%]");
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

        SpawnedDisplayEntityGroup g;
        Entity e;
        boolean result;

        boolean rideEntity = o1 instanceof SpawnedDisplayEntityGroup;

        //Group Ride Entity
        if (rideEntity){
            g = (SpawnedDisplayEntityGroup) o1;
            e = (Entity) o2;
            result = g.rideEntity(e);
        }

        //Entity Ride Group
        else{
            e = (Entity) o1;
            g = (SpawnedDisplayEntityGroup) o2;
            SpawnedDisplayEntityPart masterPart = g.getMasterPart();
            if (masterPart == null){
                return;
            }
            Entity masterEntity = masterPart.getEntity();
            if (masterEntity == null){
                return;
            }
            result = masterEntity.addPassenger(e);
        }

        if (!result){
            return;
        }

        if (controllerID != null){
            DisplayController controller = DisplayController.getController(controllerID.getSingle(event));
            if (controller == null){
                return;
            }
            for (GroupFollowProperties prop : controller.getFollowProperties()){
                prop.followGroup(g, e);
            }
            if (controller.hasStateMachine()){
                controller.getStateMachine().addGroup(g);
                g.setVerticalRideOffset(controller.getVerticalOffset());
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return obj1.toString(event, debug)+" ride "+obj2.toString(event, debug);
    }
}
