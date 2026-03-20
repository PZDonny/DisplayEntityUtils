package net.donnypz.displayentityutils.skript.player.effects;

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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Show/Hide Active Group/Part/Filter")
@Description("Show or hide an active group/part/filter to/from a player")
@Examples({"deu hide {_partfilter} from player",
        "deu show {_activepart} to player"})
@Since("2.6.2, 3.0.0 (Packet)")
public class EffActivePlayerVisibility extends Effect {

    Expression<?> object;
    Expression<Player> players;
    boolean show;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActivePlayerVisibility.class)
                        .addPattern("deu (1¦(show|reveal)|2¦hide) %activegroups/activeparts/multipartfilters% (to|from) %players%")
                        .supplier(EffActivePlayerVisibility::new)
                        .build()
        );
    }

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
        return "deu visibility for players: "+object.toString(event, debug);
    }
}
