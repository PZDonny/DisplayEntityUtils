package net.donnypz.displayentityutils.skript.group.spawnedgroup.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Merge Spawned Groups")
@Description("Merge two non-packet based groups as a single group")
@Examples({"#Merge Group B's parts into Group A. Group B will become unregistered and unusable afterwards",
        "deu merge {_spawnedgroupA} with {_spawnedgroupB}",
        "",
        "#3.4.3 and earlier",
        "merge {_spawnedgroupA} with {_spawnedgroupB}"
})
@Since("3.3.3")
public class EffSpawnedGroupMerge extends Effect {

    private Expression<SpawnedDisplayEntityGroup> groupA;
    private Expression<SpawnedDisplayEntityGroup> groupB;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffSpawnedGroupMerge.class)
                        .addPattern("deu (merge|combine) %spawnedgroup% with %spawnedgroup%")
                        .supplier(EffSpawnedGroupMerge::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        groupA = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        groupB = (Expression<SpawnedDisplayEntityGroup>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g1 = groupA.getSingle(event);
        if (g1 == null) return;
        SpawnedDisplayEntityGroup g2 = groupB.getSingle(event);
        if (g2 == null) return;
        g1.merge(g2);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "merge "+groupA.toString(event, debug) +" with "+groupB.toString(event, debug);
    }
}
