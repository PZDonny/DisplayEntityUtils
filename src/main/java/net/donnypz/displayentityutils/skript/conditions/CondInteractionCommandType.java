package net.donnypz.displayentityutils.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Interaction Command Type")
@Description("Check if a saved or spawned group has a tag")
@Examples({"if {_interactioncmd} is player cmd:", "\tbroadcast \"The player will execute the command!\""})
@Since("2.6.2")
public class CondInteractionCommandType extends Condition {

    static {
        Skript.registerCondition(CondInteractionCommandType.class, "%interactioncommand% (1¦is|2¦is(n't| not)) (:player|console) c(ommand|md)");
    }

    Expression<InteractionCommand> command;
    boolean checkingPlayer;

    @Override
    public boolean check(Event event) {
        InteractionCommand cmd = command.getSingle(event);
        if (cmd == null) return isNegated();
        return (cmd.isConsoleCommand() != checkingPlayer) == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Interaction Command Type: "+command.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.command = (Expression<InteractionCommand>) expressions[0];
        setNegated(parseResult.mark == 1);
        checkingPlayer = parseResult.hasTag("player");
        return true;
    }
}
