package net.donnypz.displayentityutils.skript.partfilter.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Part Filter Reset/Refresh/Remove")
@Description("Reset, refresh, or remove a partfilter")
@Examples({"#Ensure filtered parts are up to date with the applied filters", "refresh {_partfilter}",
        "",
        "#Reset applied filters", "reset {_partfilter}",
        "",
        "#Remove a partfilter, manually unregistering it", "remove {_partfilter}"})
@Since("2.6.2")
public class EffPartSelectionResetRefreshRemove extends Effect {

    Expression<MultiPartSelection<?>> selections;
    int option;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffPartSelectionResetRefreshRemove.class)
                        .addPattern("(1¦refresh|2¦reset|3¦remove) %multipartfilters%")
                        .supplier(EffPartSelectionResetRefreshRemove::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        selections = (Expression<MultiPartSelection<?>>) expressions[0];
        option = parseResult.mark;
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (MultiPartSelection<?> s : selections.getArray(event)){
            if (s == null){
                continue;
            }
            if (option == 1){ //refresh
                s.refresh();
            }
            else if (option == 2){ //reset
                s.reset(true);
            }
            else{ //remove
                s.remove();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "refresh/reset/remove part filter: "+selections.toString(event, debug);
    }
}
