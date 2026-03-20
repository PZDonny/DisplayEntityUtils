package net.donnypz.displayentityutils.skript.group.activegroup.conditions;

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

@Name("Active Group Is Registered?")
@Description("Check if an active group is registered and usable")
@Examples({"if deu {_group} is registered:",
        "\tbroadcast \"This group is registered\"",
        "",
        "#3.4.3 and earlier",
        "if {_group} is registered:",
        "\tbroadcast \"This group is registered!\""})
@Since("2.6.2, 3.5.0 (Packet)")
public class CondActiveGroupIsRegistered extends Condition {

    Expression<SpawnedDisplayEntityGroup> group;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondActiveGroupIsRegistered.class)
                        .addPattern("deu [group] %activegroup% (1¦is|2¦is(n't| not)) registered")
                        .supplier(CondActiveGroupIsRegistered::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isRegistered() != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group registered: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
