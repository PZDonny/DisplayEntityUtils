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
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group/Part/Selection Player Visibility")
@Description("Show/Hide a DisplayEntityUtils object to/from a player")
@Examples({"deu hide {_partselection} from player", "deu show {_spawnedpart} to player"})
@Since("2.6.2")
public class EffSpawnedPlayerVisibility extends Effect {
    static {
        Skript.registerEffect(EffSpawnedPlayerVisibility.class,"deu (1¦(show|reveal)|2¦hide) %spawnedgroups/spawnedparts/partselections% (to|from) %players%");
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
        Spawned[] spawned = (Spawned[]) object.getArray(event);
        Player[] plrs = players.getArray(event);
        for (Spawned s : spawned){
            if (s == null) continue;
            for (Player o : plrs){
                if (o == null) continue;
                if (show){
                    s.showToPlayer(o);
                }
                else{
                    s.hideFromPlayer(o);
                }
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "visibility for players: "+object.toString(event, debug);
    }
}
