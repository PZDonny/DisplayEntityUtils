package net.donnypz.displayentityutils.skript.group.spawnedgroup.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.events.GroupUnregisteredEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Override Group Unregister Despawn")
@Description("Override the possible despawning of part entities when a non-packet group is unregistered")
@Examples({"on display group unregistered:",
        "\tdisallow group despawn"})
@Since("2.6.2")
public class EffSpawnedGroupUnregisterOverride extends Effect {

    boolean despawn;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffSpawnedGroupUnregisterOverride.class)
                        .addPattern("[deu] [:dis]allow group despawn")
                        .supplier(EffSpawnedGroupUnregisterOverride::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        despawn = !parseResult.hasTag("dis");
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (event instanceof GroupUnregisteredEvent e){
            e.setDespawn(despawn);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "override group unregister despawn: "+despawn;
    }
}
