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

@Name("Spawned Group Is Visible By Default?")
@Description("Check if a spawned group is visible by default")
@Examples({ "if deu {_activegroup} is visible by default:", "\tbroadcast \"Players can see the group by default!\"",
        "",
        "#3.4.3 and earlier",
        "if {_activegroup} is visible by default:", "\tbroadcast \"Players can see the group by default!\""})
@Since("2.6.2")
public class CondActiveGroupIsVisibleByDefault extends Condition {

    Expression<SpawnedDisplayEntityGroup> group;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondActiveGroupIsVisibleByDefault.class)
                        .addPattern("deu [group] %activegroup% (1¦is|2¦is(n't| not)) visible by default")
                        .supplier(CondActiveGroupIsVisibleByDefault::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isVisibleByDefault() != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Spawned group visible by default: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
