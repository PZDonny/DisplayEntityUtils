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
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Part Selection Filter Part Tags")
@Description("Filter part tags in a part selection")
@Examples({"#Filter parts with the tag \"head\" but exclude if they have \"eye\"",
        "add filter to {_selection} with tag \"head\" and without tag \"eye\"",
        "",
        "#Filter all parts except ones with the tag \"head\"",
        "add filter to {_selection} without tag \"head\""})
@Since("2.6.2")
public class EffPartSelectionFilterTags extends Effect {
    static {
        Skript.registerEffect(EffPartSelectionFilterTags.class,"add filter to %multipartselection% [in:with [part( |-)?]tag[s] %-strings%] [ex:[and ]without [part( |-)?]tag[s] %-strings%]");
    }

    Expression<MultiPartSelection> selection;
    Expression<String> includedTags;
    Expression<String> excludedTags;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        selection = (Expression<MultiPartSelection>) expressions[0];
        if (parseResult.hasTag("in")){
            includedTags = (Expression<String>) expressions[1];
        }
        if (parseResult.hasTag("ex")){
            excludedTags = (Expression<String>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        MultiPartSelection sel = selection.getSingle(event);
        if (sel == null || (includedTags == null && excludedTags == null)){
            return;
        }
        PartFilter builder = new PartFilter();
        if (includedTags != null){
            String[] tags = includedTags.getArray(event);
            if (tags != null){
                for (String tag : tags){
                    builder.includePartTag(tag);
                }
            }
        }

        if (excludedTags != null){
            String[] tags = excludedTags.getArray(event);
            if (tags != null){
                for (String tag : tags){
                    builder.excludePartTag(tag);
                }
            }
        }
        sel.applyFilter(builder, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add part selection tag filter: "+selection.toString(event, debug);
    }
}
