package net.donnypz.displayentityutils.skript.group.activegroup.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group Parts Filter")
@Description("Create a selection, containing the parts of a active group, which can later be filtered")
@Examples({
        "set {_partfilter} to a new part filter from {_activegroup}",
        "",
        "#3.3.3 and earlier",
        "set {_partselection} to a new part selection using {_activegroup}",
        "set {_partselection} to a new part selection of {_activegroup}",
        "",
        "#2.7.7 and earlier",
        "set {_partselection} to a new spawned part selection from {_spawnedgroup}",
})
@Since("2.6.2, 3.3.4 (Filter Syntax)")
public class ExprActiveGroupCreatePartFilter extends SimpleExpression<MultiPartSelection> {

    private Expression<ActiveGroup> group;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveGroupCreatePartFilter.class, MultiPartSelection.class)
                        .addPatterns("[a] [new] part[ |-](filter|selection) [from|using|of] %activegroup%")
                        .supplier(ExprActiveGroupCreatePartFilter::new)
                        .build()
        );
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MultiPartSelection> getReturnType() {
        return MultiPartSelection.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "create part filter: "+group.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        return true;
    }

    @Override
    protected MultiPartSelection[] get(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null){
            return null;
        }

        return new MultiPartSelection[]{g.createPartSelection()};
    }
}
