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
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.Packeted;
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group/Part/Selection Player Visibility")
@Description("Show/Hide a DisplayEntityUtils object to/from a player")
@Examples({"deu hide {_partselection} from player",
        "deu show {_spawnedpart} to player",
        "",
        "#3.0.0 or later",
        "deu show {_packetgroup} to {_players::*}"})
@Since("2.6.2")
public class EffActivePlayerVisibility extends Effect {
    static {
        Skript.registerEffect(EffActivePlayerVisibility.class,"deu (1¦(show|reveal)|2¦hide) %spawnedgroups/spawnedparts/partselections/packetgroups/packetparts/packetpartselections% (to|from) %players%");
    }

    Expression<?> object;
    Expression<Player> players;
    boolean show;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        players = (Expression<Player>) expressions[1];
        show = parseResult.mark == 1;
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object[] objects = object.getArray(event);
        Player[] plrs = players.getArray(event);
        for (Object o : objects){
            if (o instanceof Spawned spawned){
                for (Player p : plrs){
                    if (p == null) continue;
                    if (show){
                        spawned.showToPlayer(p);
                    }
                    else{
                        ((Active) spawned).hideFromPlayer(p);
                    }
                }
            }
            else if (o instanceof Packeted packeted){
                for (Player p : plrs){
                    if (p == null) continue;
                    if (show){
                        packeted.showToPlayer(p, GroupSpawnedEvent.SpawnReason.SKRIPT);
                    }
                    else{
                        ((Active) packeted).hideFromPlayer(p);
                    }
                }
            }

        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "visibility for players: "+object.toString(event, debug);
    }
}
