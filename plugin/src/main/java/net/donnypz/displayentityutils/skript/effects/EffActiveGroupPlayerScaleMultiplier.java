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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Scale Multiplier")
@Description("Set the scale multiplier that will be applied to a player's viewing of a given active group. This is applied on top of a group's current scale multiplier." +
        "\nThe position of an Interaction entity may be incorrect if it is not packet-based.")
@Examples({"set extra scale multiplier of {_activegroup} to 1.75 for {_player}",
        "set extra scale multiplier of {_activegroup} to 2 for {_player} and ignore interaction entites",
        "",
        "#Reset",
        "set extra scale multiplier of {_activegroup} to 1 for {_player}"})
@Since("3.4.2")
public class EffActiveGroupPlayerScaleMultiplier extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupPlayerScaleMultiplier.class,"set [extra|add(itional|ed)] scale [multiplier] of %activegroup% to %number% for %players% [i:and ignore interaction[s] [entit(y|ies)]]");
    }

    Expression<ActiveGroup> activeGroup;
    Expression<Number> scale;
    boolean ignore;
    Expression<Player> players;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        activeGroup = (Expression<ActiveGroup>) expressions[0];
        scale = (Expression<Number>) expressions[1];
        ignore = parseResult.hasTag("i");
        players = (Expression<Player>) expressions[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = activeGroup.getSingle(event);
        if (g == null){
            return;
        }
        float scaleNum = scale != null ? scale.getSingle(event).floatValue() : 1f;

        for (Player p : players.getAll(event)){
            if (scaleNum == 1){
                g.unsetPlayerScaleMultiplier(p);
            }
            else{
                g.setPlayerScaleMultiplier(p, scaleNum, !ignore);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "extra scale multiplier of "+ scale.toString(event, debug)+ "on "+activeGroup.toString(event, debug)+" for "+players.toString(event, debug);
    }
}