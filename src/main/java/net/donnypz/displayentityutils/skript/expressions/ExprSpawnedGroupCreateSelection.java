package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Create Spawned Part Selection")
@Description("Create a spawned part selection containing the parts of a spawned group")
@Examples({"set {_selection} to a new spawned part selection from {_spawnedgroup}"})
@Since("2.6.2")
public class ExprSpawnedGroupCreateSelection extends SimpleExpression<SpawnedPartSelection> {

    static{
        Skript.registerExpression(ExprSpawnedGroupCreateSelection.class, SpawnedPartSelection.class, ExpressionType.SIMPLE, "[a] [new] [spawned] part[ |-]selection [from|using|of] %spawnedgroup%");
    }

    Expression<SpawnedDisplayEntityGroup> group;

    @Override
    protected SpawnedPartSelection @Nullable [] get(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null){
            return null;
        }

        return new SpawnedPartSelection[]{new SpawnedPartSelection(g)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends SpawnedPartSelection> getReturnType() {
        return SpawnedPartSelection.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawned part selection: "+group.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        return true;
    }
}
