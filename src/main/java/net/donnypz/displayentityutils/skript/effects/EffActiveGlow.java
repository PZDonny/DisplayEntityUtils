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
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Glow")
@Description("Set the glowing of an active group/part/selection")
@Examples({"#Before 3.0.0",
            "make {_spawnedgroup} glow for 35 ticks", "make {_partselection} glow with interactions and with marker particles",
            "set {_spawnedpart} to unglowing"
            ,""
            ,"#3.0.0 and Later"
            ,"make {_spawnedgroup} glow for 35 ticks"
            ,"make {_packetgroup} glow for 20 ticks for {_players}"
            ,"set {_part} to unglowing for {_player}"})
@Since("2.6.2")
public class EffActiveGlow extends Effect {
    static {
        Skript.registerEffect(EffActiveGlow.class,"[deu ](make|set) %activegroups/activeparts/activepartselections% (1¦glow[ing] [t:for %-timespan%] [p:for %-players%]|2¦unglow[ing] [p:for %-players%])");
    }

    Expression<?> object;
    Expression<Timespan> timespan;
    Expression<Player> players;
    boolean glow;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        glow = parseResult.mark == 1;
        if (glow){
            if (parseResult.hasTag("t")){
                timespan = (Expression<Timespan>) expressions[1];
            }
            if (parseResult.hasTag("p")){
                players = (Expression<Player>) expressions[2];
            }
        }
        else{
            if (parseResult.hasTag("p")){
                players = (Expression<Player>) expressions[1];
            }
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        long ticks = -1;
        if (timespan != null){
            Timespan ts = timespan.getSingle(event);
            if (ts != null) ticks = ts.getAs(Timespan.TimePeriod.TICK);
        }

        for (Object o : object.getArray(event)){
            if (!(o instanceof Active a)) continue;
            if (!glow){
                if (players == null){
                    a.unglow();
                }
                else {
                    for (Player p : players.getArray(event)) {
                        a.unglow(p);
                    }
                }
            }
            else{
                if (players == null){
                    if (ticks == -1){
                        a.glow();
                    }
                    else{
                        a.glow(ticks);
                    }
                }
                else{
                    for (Player player : players.getArray(event)){
                        a.glow(player, Math.max(-1, ticks));
                    }
                }
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "toggle part glow: "+object.toString(event, debug);
    }
}
