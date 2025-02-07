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
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Group Spawn Settings Visible")
@Description("Set the visibility property of a group spawn setting")
@Examples({"set {_setting} to visible by default", "set {_setting}'s interactions to invisible by default"})
@Since("2.6.2")
public class EffGroupSpawnSettingVisibility extends Effect {
    static {
        Skript.registerEffect(EffGroupSpawnSettingVisibility.class,"(make|set) %groupspawnsetting%['s] [:interactions] [to] [:in]visible by default");
    }

    Expression<GroupSpawnSettings> settings;
    Expression<Player> players;
    boolean invisible;
    boolean forInteractions;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        settings = (Expression<GroupSpawnSettings>) expressions[0];
        forInteractions = parseResult.hasTag("interactions");
        invisible = parseResult.hasTag("in");
        if (expressions.length == 2){
            players = (Expression<Player>) expressions[1];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        GroupSpawnSettings s = settings.getSingle(event);
        if (forInteractions){
            s.hideInteractionsByDefault(invisible);
        }
        else{
            s.visibleByDefault(!invisible, null);
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "visible by default for spawn settings: "+settings.toString(event, debug);
    }
}
