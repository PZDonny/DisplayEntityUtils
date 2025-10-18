package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
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

@Name("Part Filter/Selection Reset/Refresh/Remove")
@Description("Reset, refresh, or remove a part selection")
@Examples({"#Ensure selected parts are up to date with the applied filters", "refresh {_selection}",
        "",
        "#Reset selection filters", "reset {_selection}",
        "",
        "#Remove a part selection, manually unregistering it", "remove {_selection}"})
@Since("2.6.2")
public class EffPartSelectionResetRefreshRemove extends Effect {
    static {
        Skript.registerEffect(EffPartSelectionResetRefreshRemove.class,"(1¦refresh|2¦reset|3¦remove) %multipartfilters%");
    }

    Expression<MultiPartSelection<?>> selections;
    int option;

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
        return "refresh/reset/remove part selection: "+selections.toString(event, debug);
    }
}
