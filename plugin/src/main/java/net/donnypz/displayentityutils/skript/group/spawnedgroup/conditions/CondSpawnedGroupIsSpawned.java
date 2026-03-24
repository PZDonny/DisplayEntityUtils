package net.donnypz.displayentityutils.skript.group.spawnedgroup.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawned Group Is Spawned?")
@Description("Check if a spawned group is present in the game world")
@Examples({"if {_group} is spawned:", "\tbroadcast \"The group is spawned in the world!\""})
@Since("2.6.2")
public class CondSpawnedGroupIsSpawned extends Condition {

    Expression<SpawnedDisplayEntityGroup> group;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondSpawnedGroupIsSpawned.class)
                        .addPattern("%spawnedgroup% (1¦is|2¦is(n't| not)) spawned")
                        .supplier(CondSpawnedGroupIsSpawned::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isSpawned() != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Is group spawned: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
