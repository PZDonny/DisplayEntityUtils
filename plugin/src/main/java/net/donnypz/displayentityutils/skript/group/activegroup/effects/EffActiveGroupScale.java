package net.donnypz.displayentityutils.skript.group.activegroup.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Scale Active Group")
@Description("Change the scale multiplier of an active group")
@Examples({
        "deu scale {_activegroup} by 2 over 0 ticks",
        "deu scale {_activegroup} by 5 over 0 ticks and scale interactions",
        "",
        "#Additional scaling for player (group's scale * 1.25)",
        "deu scale {_activegroup}'s by to 1.25 for {_player} ",
        "",
        "#3.4.3 and earlier",
        "set {_activegroup}'s scale multiplier to 7 over 0 ticks",
        "set {_activegroup}'s scale multiplier 0.5 over 10 ticks",
        "",

})
@Since("2.6.3, 3.0.0 (Packet), 3.5.0 (Player)")
public class EffActiveGroupScale extends Effect {

    Expression<ActiveGroup> group;
    Expression<Number> multiplier;
    Expression<Timespan> timespan;
    Expression<Player> players;
    boolean scaleInteractions;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupScale.class)
                        .addPattern("deu scale %activegroup%['s] by [multiplier] %number% (1¦for %-players%|2¦(for|over) %-timespan%) [i:and [scale] interactions]")
                        .supplier(EffActiveGroupScale::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        multiplier = (Expression<Number>) expressions[1];
        if (parseResult.mark == 1){
            players = (Expression<Player>) expressions[2];
        }
        else if (parseResult.mark == 2){
            timespan = (Expression<Timespan>) expressions[3];
        }
        scaleInteractions = parseResult.hasTag("i");
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup[] groupArr = group.getArray(event);
        Number n = multiplier.getSingle(event);


        if (groupArr == null || n == null) return;

        int ticks = timespan == null ? 0 : (int) timespan.getSingle(event).getAs(Timespan.TimePeriod.TICK);
        Player[] playerArr = players == null ? null : players.getArray(event);

        float multiplier = n.floatValue();
        for (ActiveGroup g : groupArr){
            if (playerArr != null){
                for (Player p : playerArr){
                    DEUUser.getOrCreateUser(p).setScaleMultiplier(g, multiplier, scaleInteractions);
                }
            }
            else{
                g.scale(n.floatValue(), ticks, scaleInteractions);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "scale multiplier: "+group.toString(event, debug);
    }
}
