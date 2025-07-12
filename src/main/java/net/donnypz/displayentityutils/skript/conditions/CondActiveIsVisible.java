package net.donnypz.displayentityutils.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group is Visible?")
@Description("Check if a player can see an active group or active part")
@Examples({"if {_player} can see the deu {_group}:", "\tbroadcast\"The player can see this group\""})
@Since("3.0.0")
public class CondActiveIsVisible extends Condition {

    static {
        Skript.registerCondition(CondActiveIsVisible.class, "%player% (1¦can|2¦can(t|( )not)) see [the] [deu] %activegroup/activepart%");
    }

    Expression<Player> player;
    Expression<?> active;

    @Override
    public boolean check(Event event) {
        Player p = player.getSingle(event);
        Object o = active.getSingle(event);
        if (p == null || o == null) return isNegated();
        if (o instanceof ActiveGroup ag){
            return ag.isTrackedBy(p) == isNegated();
        }
        else if (o instanceof SpawnedDisplayEntityPart sp){
            return p.canSee(sp.getEntity()) == isNegated();
        }
        else if (o instanceof PacketDisplayEntityPart pp){
            return pp.isTrackedBy(p) == isNegated();
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group is visible?: "+ active.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.player = (Expression<Player>) expressions[0];
        this.active = (Expression<ActiveGroup>) expressions[1];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
