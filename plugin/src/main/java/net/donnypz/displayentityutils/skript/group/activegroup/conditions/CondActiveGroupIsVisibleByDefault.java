package net.donnypz.displayentityutils.skript.group.activegroup.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group - Is Visible By Default")
@Description("Check if an active group is visible by default")
@Examples({ "if deu {_activegroup} is visible by default:", "\tbroadcast \"Players can see the group by default!\"",
        "",
        "#3.4.3 and earlier",
        "if {_activegroup} is visible by default:", "\tbroadcast \"Players can see the group by default!\""})
@Since("2.6.2")
public class CondActiveGroupIsVisibleByDefault extends Condition {

    Expression<ActiveGroup<?>> group;

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
        ActiveGroup<?> g = group.getSingle(event);
        if (g instanceof PacketDisplayEntityGroup pg){
            return pg.isAutoShow() != isNegated();
        }
        else if (g instanceof SpawnedDisplayEntityGroup sg){
            return sg.isVisibleByDefault() != isNegated();
        }
        return isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Active group visible by default: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<ActiveGroup<?>>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
