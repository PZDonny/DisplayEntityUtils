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
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Entity Ride Spawned Group")
@Description("Make an entity ride a spawned group")
@Examples({"deu make {_entity} ride {_spawnedgroup}", "deu make {_entity} mount {_spawnedgroup} using controller with id \"mycontroller\""})
@Since("2.6.2")
public class EffEntityRideSpawnedGroup extends Effect {
    static {
        Skript.registerEffect(EffEntityRideSpawnedGroup.class,"[deu] make %entity% (mount|ride) %spawnedgroup% [c:using controller [with id] %string%]");
    }

    Expression<Entity> entity;
    Expression<SpawnedDisplayEntityGroup> group;
    Expression<String> controllerID;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        entity = (Expression<Entity>) expressions[0];
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[1];
        if (parseResult.hasTag("c")){
            controllerID = (Expression<String>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        Entity e = entity.getSingle(event);
        if (g == null || e == null){
            return;
        }
        g.rideEntity(e);
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
                g.setVerticalOffset(controller.getVerticalOffset());
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return group.toString(event, debug)+" ride entity: "+entity.toString(event, debug);
    }
}
