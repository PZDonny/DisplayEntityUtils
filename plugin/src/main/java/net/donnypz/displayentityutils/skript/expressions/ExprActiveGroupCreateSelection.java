package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Create Part Filter/Selection")
@Description("Create a selection, containing the parts of a active group, which can later be filtered")
@Examples({"#2.7.7 and earlier",
        "set {_selection} to a new spawned part selection from {_spawnedgroup}",
        "",
        "#3.0.0 and later",
        "set {_selection} to a new part selection using {_packetgroup}",
        "set {_selection} to a new part selection of {_spawnedgroup}",
        "",
        "#3.3.4 and later",
        "set {_selection} to a new part selection from {_packetgroup}"})
@Since("2.6.2, 3.3.4 (Filter Syntax)")
public class ExprActiveGroupCreateSelection extends SimpleExpression<MultiPartSelection> {

    static{
        Skript.registerExpression(ExprActiveGroupCreateSelection.class, MultiPartSelection.class, ExpressionType.SIMPLE, "[a] [new] part[ |-](filter|selection) [from|using|of] %activegroup%");
    }

    private Expression<ActiveGroup> group;

    @Override
    protected MultiPartSelection @Nullable [] get(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null){
            return null;
        }

        return new MultiPartSelection[]{g.createPartSelection()};
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
}
