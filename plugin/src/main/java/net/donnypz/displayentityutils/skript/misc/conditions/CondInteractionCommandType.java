package net.donnypz.displayentityutils.skript.misc.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Interaction Command Type")
@Description("Check if a saved or spawned group has a tag")
@Examples({"if {_interactioncmd} is player cmd:", "\tbroadcast \"The player will execute the command!\""})
@Since("2.6.2")
public class CondInteractionCommandType extends Condition {

    Expression<InteractionCommand> command;
    boolean isPlayer;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondInteractionCommandType.class)
                        .addPattern("%interactioncommand% (1¦is|2¦is(n't| not)) (:player|console) c(ommand|md)")
                        .supplier(CondInteractionCommandType::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        InteractionCommand cmd = command.getSingle(event);
        if (cmd == null) return isNegated();
        return (cmd.isConsoleCommand() == isPlayer) == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Interaction Command Type: "+command.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.command = (Expression<InteractionCommand>) expressions[0];
        setNegated(parseResult.mark == 2);
        isPlayer = parseResult.hasTag("player");
        return true;
    }
}
