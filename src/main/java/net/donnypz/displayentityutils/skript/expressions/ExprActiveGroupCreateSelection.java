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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Create Spawned Part Selection")
@Description("Create a spawned or packet part selection containing the parts of a active group")
@Examples({"#2.7.7 and earlier",
        "set {_selection} to a new spawned part selection from {_spawnedgroup}",
        "",
        "#3.0.0 and later",
        "set {_selection} to a new part selection using {_packetgroup}",
        "set {_selection} to a new part selection of {_spawnedgroup}"})
@Since("2.6.2")
public class ExprActiveGroupCreateSelection extends SimpleExpression<ActivePartSelection> {

    static{
        Skript.registerExpression(ExprActiveGroupCreateSelection.class, ActivePartSelection.class, ExpressionType.SIMPLE, "[a] [new] part[ |-]selection [from|using|of] %activegroup%");
    }

    Expression<ActiveGroup> group;

    @Override
    protected ActivePartSelection @Nullable [] get(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null){
            return null;
        }

        return new ActivePartSelection[]{g.createPartSelection()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ActivePartSelection> getReturnType() {
        return ActivePartSelection.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "active part selection: "+group.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        return true;
    }
}
