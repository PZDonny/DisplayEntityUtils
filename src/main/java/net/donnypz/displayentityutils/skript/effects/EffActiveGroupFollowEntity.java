package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.FollowType;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Follow Entity Direction")
@Description("Make an active group respect an entity's looking direction")
@Examples({
        "make {_activegroup} follow {_entity} with ft_body",
        "make {_activegroup} follow {_entity} with ft_pitch and flip group",
        "make {_activegroup} follow {_entity} with ft_yaw and with teleport duration of 2",
        "make {_activegroup} follow {_entity} with ft_pitch_and_yaw and after death despawn after 2 seconds",
        "",
        "#Combined",
        "make {_activegroup} follow {_entity} with ft_body and flip group and using smoothness of 2 and despawn after 1 second"})
@Since("3.2.1")
public class EffActiveGroupFollowEntity extends Effect {

    static {
        Skript.registerEffect(EffActiveGroupFollowEntity.class,"make %activegroups% (follow|respect) %entity% (with|using) %followtype% " +
                "[f:[and] flip group] [t:[and] (with|using) (teleport[ation] duration|smoothness) [of] %-number%] [d:[and] [after death] despawn after %-timespan%]");
    }

    Expression<ActiveGroup<?>> group;
    Expression<Entity> entity;
    Expression<FollowType> followType;
    Expression<Number> tpDuration;
    boolean flip;
    Expression<Timespan> despawnTimespan;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup<?>>) expressions[0];
        entity = (Expression<Entity>) expressions[1];
        followType = (Expression<FollowType>) expressions[2];
        flip = parseResult.hasTag("f");
        if (parseResult.hasTag("t")) {
            tpDuration = (Expression<Number>) expressions[3];
        }
        if (parseResult.hasTag("d")) {
            despawnTimespan = (Expression<Timespan>) expressions[4];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup<?>[] gs = group.getArray(event);
        Entity e = entity.getSingle(event);
        FollowType type = followType.getSingle(event);
        int tpDur = tpDuration == null ? 0 : tpDuration.getSingle(event).intValue();
        int despawn = despawnTimespan == null ? -1 : (int) despawnTimespan.getSingle(event).getAs(Timespan.TimePeriod.TICK);
        if (gs == null || e == null || type == null) return;

        for (ActiveGroup<?> g : gs){
            g.followEntityDirection(e, GroupFollowProperties.builder(type)
                    .setFlip(flip)
                    .setTeleportationDuration(tpDur)
                    .setUnregisterDelay(despawn)
                    .build());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return group.toString(event, debug)+" follow "+ entity.toString(event, debug);
    }
}
