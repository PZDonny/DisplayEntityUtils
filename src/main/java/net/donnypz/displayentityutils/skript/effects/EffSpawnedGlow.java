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
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Glow")
@Description("Set the glowing of a spawned group/part/selection")
@Examples({"make {_spawnedgroup} glow for 35 ticks", "make {_partselection} glow with interactions and with marker particles", "set {_spawnedpart} to unglowing"})
@Since("2.6.2")
public class EffSpawnedGlow extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGlow.class,"[deu ](make|set) %spawnedgroups/spawnedparts/partselections% (1¦glow[ing] [t:for %-timespan%] [i:[and ]with interaction[s]] [p:[and ]with [marker] particle[s]]|2¦unglow[ing])");
    }

    Expression<Object> object;
    Expression<Timespan> timespan;
    boolean glow;
    boolean withInteractions = false;
    boolean withParticles = false;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = (Expression<Object>) expressions[0];
        glow = parseResult.mark == 1;
        if (glow){
            if (parseResult.hasTag("t")){
                timespan = (Expression<Timespan>) expressions[1];
            }

            withInteractions = parseResult.hasTag("i");
            withParticles = parseResult.hasTag("p");
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

        Spawned[] objects = (Spawned[]) object.getArray(event);
        if (!glow){
            for (Spawned s : objects){
                if (s == null) continue;
                s.unglow();
            }
            return;
        }
        for (Spawned s : objects){
            if (s instanceof SpawnedDisplayEntityGroup group){
                if (ticks == -1){
                    group.glow(!withInteractions, !withParticles);
                }
                else{
                    group.glow(ticks, !withInteractions, !withParticles);
                }
            }
            else if (s instanceof SpawnedPartSelection sel){
                if (ticks == -1){
                    sel.glow(!withInteractions, !withParticles);
                }
                else{
                    sel.glow(ticks, !withInteractions, !withParticles);
                }
            }
            else if (s instanceof SpawnedDisplayEntityPart part){
                if (ticks == -1){
                    part.glow(!withParticles);
                }
                else{
                    part.glow(ticks, !withParticles);
                }
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "toggle part glow: "+object.toString(event, debug);
    }
}
