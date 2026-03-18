package net.donnypz.displayentityutils.skript.parts.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Interaction Part Has Commands?")
@Description("Check if an interaction part/entity has commands that will run when clicked")
@Examples({"if {_activepart} is deu_interaction:",
        "\tif {_activepart} has click commands:",
        "\t\tbroadcast \"The interaction has commands!\"",
})
@Since("3.5.0")
public class CondInteractionHasCommands extends Condition {

    Expression<?> object;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondInteractionHasCommands.class)
                        .addPattern("%activepart/entities% (1¦has|2¦(has no|does(n't| not) have)) [click] commands")
                        .supplier(CondInteractionHasCommands::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        Object obj = object.getSingle(event);
        switch (obj) {
            case ActivePart p -> {
                return p.getInteractionCommands().isEmpty() == isNegated();
            }
            case Interaction interaction -> {
                return DisplayUtils.getInteractionCommands(interaction).isEmpty() == isNegated();
            }
            case null, default -> {
                return isNegated();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "interaction part has commands?: "+object.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.object = expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
