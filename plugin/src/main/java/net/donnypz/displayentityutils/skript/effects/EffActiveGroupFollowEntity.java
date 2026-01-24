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
@Description("Make an active group respect an entity's looking direction. " +
        "\nIN VERSIONS BELOW v3.3.5, EACH TYPE IS PREFIXED WITH \"ft\": \"ft_body\", \"ft_pitch\", \"ft_yaw\", and \"ft_pitch_and_yaw\"!" +
        "\n3.4.2+ replaces \"pitch_and_yaw\" with \"pitch and yaw\"")
@Examples({
        "make {_activegroup} follow {_entity} using body yaw",
        "make {_activegroup} follow {_entity} using pitch and flip group",
        "make {_activegroup} follow {_entity} using yaw and with teleport duration of 2",
        "make {_activegroup} follow {_entity} using pitch_and_yaw and after death despawn after 2 seconds",
        "",
        "#Combined",
        "make {_activegroup} follow {_entity} using yaw and flip group and with smoothness of 2 and despawn after 1 second"})
@Since({"3.2.1, 3.3.5 (No \"ft\" prefix)"})
public class EffActiveGroupFollowEntity extends Effect {

    static {
        Skript.registerEffect(EffActiveGroupFollowEntity.class,"make %activegroups% (follow|respect) %entity% (with|using) (1¦pitch|2¦yaw|3¦pitch and yaw|4¦body [yaw]) " +
                "[f: [and] flip group] [t: [and] (with|using) (teleport[ation] duration|smoothness) [of] %-number%] [d: [and] [after death] despawn after %-timespan%]"); //Keep the space between tag and "[and]"
    }

    Expression<ActiveGroup<?>> group;
    Expression<Entity> entity;
    FollowType followType;
    Expression<Number> tpDuration;
    boolean flip;
    Expression<Timespan> despawnTimespan;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup<?>>) expressions[0];
        entity = (Expression<Entity>) expressions[1];
        flip = parseResult.hasTag("f");
        if (parseResult.hasTag("t")) {
            tpDuration = (Expression<Number>) expressions[3];
        }
        if (parseResult.hasTag("d")) {
            despawnTimespan = (Expression<Timespan>) expressions[4];
        }
        switch (parseResult.mark){
            case 1 -> {
                followType = FollowType.PITCH;
            }
            case 2 -> {
                followType = FollowType.YAW;
            }
            case 3 -> {
                followType = FollowType.PITCH_AND_YAW;
            }
            case 4 -> {
                followType = FollowType.BODY;
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup<?>[] gs = group.getArray(event);
        Entity e = entity.getSingle(event);
        FollowType type = followType;
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
