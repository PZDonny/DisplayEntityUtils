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
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Group Spawn Settings Persistence")
@Description("Set the persistence property of a group spawn setting")
@Examples({"set {_setting} to persistent by default", "set {_setting} to not persistent by default"})
@Since("2.6.3")
public class EffGroupSpawnSettingPersistence extends Effect {
    static {
        Skript.registerEffect(EffGroupSpawnSettingPersistence.class,"(make|set) %groupspawnsetting% [to] [:not] persistent by default");
    }

    Expression<GroupSpawnSettings> settings;
    boolean persistent;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        settings = (Expression<GroupSpawnSettings>) expressions[0];
        persistent = !parseResult.hasTag("not");
        return true;
    }

    @Override
    protected void execute(Event event) {
        GroupSpawnSettings s = settings.getSingle(event);
        s.persistentByDefault(persistent);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "persistent by default for spawn settings: "+settings.toString(event, debug);
    }
}
