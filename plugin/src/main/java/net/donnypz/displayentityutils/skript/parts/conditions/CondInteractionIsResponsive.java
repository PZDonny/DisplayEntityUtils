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
import org.bukkit.entity.Interaction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Is Interaction Part Responsive?")
@Description("Check if an interaction part/entity is responsive")
@Examples({"if {_activepart} is deu_interaction:",
        "\tif {_activepart} is deu responsive:",
        "\t\tbroadcast \"The interaction is responsive!\"",
        "\telse:",
        "\t\tbroadcast \"The interaction is not responsive!\""
})
@Since("3.5.0")
public class CondInteractionIsResponsive extends Condition {

    Expression<?> object;

    public static void register(SyntaxRegistry registry){

        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondInteractionIsResponsive.class)
                        .addPattern("%activepart/entities% (1¦is|2¦is(n't| not)) deu responsive")
                        .supplier(CondInteractionIsResponsive::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        Object obj = object.getSingle(event);
        switch (obj) {
            case ActivePart p -> {
                return p.isInteractionResponsive() != isNegated();
            }
            case Interaction interaction -> {
                return interaction.isResponsive() != isNegated();
            }
            case null, default -> {
                return isNegated();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "is interaction part responsive?: "+object.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.object = expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
