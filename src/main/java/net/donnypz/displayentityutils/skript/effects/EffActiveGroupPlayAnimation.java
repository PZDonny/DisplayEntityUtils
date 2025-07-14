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
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@Name("Animate Active Group")
@Description("Play an animation on an active group with a display animator")
@Examples({"play animation on {_spawnedgroup} using {_displayanimator}",
        "",
        "#3.0.0 or later",
        "start packet animation on {_packetgroup} with {_displayanimator} starting at frame 3",
        "start packet animation on {_group} using {_displayanimator} for {_player}"})
@Since("2.6.2")
public class EffActiveGroupPlayAnimation extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupPlayAnimation.class,"(start|play) [p:packet] anim[ation] on %activegroup% (using|with) %displayanimator% [frame:[starting ](at|on) frame %-number%] [f:for %-players%]");
    }

    Expression<ActiveGroup> group;
    Expression<DisplayAnimator> animator;
    Expression<Number> frame;
    Expression<Player> players;
    boolean packet;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        animator = (Expression<DisplayAnimator>) expressions[1];
        if (parseResult.hasTag("frame")){
            frame = (Expression<Number>) expressions[2];
        }
        packet = parseResult.hasTag("p");
        if (parseResult.hasTag("f")){
            players = (Expression<Player>) expressions[3];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = group.getSingle(event);
        DisplayAnimator a = animator.getSingle(event);
        if (g == null || a == null){
            return;
        }
        int frameNum = frame != null ? frame.getSingle(event).intValue() : 0;
        Collection<Player> playerColl;
        if (players != null){
            playerColl = Arrays.stream(players.getAll(event)).toList();
        }
        else{
            playerColl = null;
        }
        if (packet){
            if (playerColl != null){
                a.play(playerColl, g, frameNum);
            }
            else{
                a.playUsingPackets(g, frameNum);
            }

        }
        else{
            if (g instanceof SpawnedDisplayEntityGroup sg){
                a.play(sg, frameNum);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "play animation: "+animator.toString(event, debug);
    }
}
