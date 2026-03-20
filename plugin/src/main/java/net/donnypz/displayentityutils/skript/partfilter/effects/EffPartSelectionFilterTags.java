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
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Part Filter Part Tags")
@Description("Filter parts by part tags in a partfilter")
@Examples({
        "#Filter parts with the tag \"head\" but exclude if they have \"eye\"",
        "deu filter {_partfilter} with tag \"head\" and without tag \"eye\"",
        "",
        "#Filter all parts except ones with the tag \"head\"",
        "deu filter {_partfilter} without tag \"head\"",
        "",
        "",
        "#3.4.3 and earlier",
        "#Filter parts with the tag \"head\" but exclude if they have \"eye\"",
        "add filter to {_partfilter} with tag \"head\" and without tag \"eye\"",
        "",
        "#Filter all parts except ones with the tag \"head\"",
        "add filter to {_partfilter} without tag \"head\""
})
@Since("2.6.2")
public class EffPartSelectionFilterTags extends Effect {

    Expression<MultiPartSelection> selection;
    Expression<String> includedTags;
    Expression<String> excludedTags;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffPartSelectionFilterTags.class)
                        .addPattern("deu filter %multipartfilter% [in:with [part( |-)?]tag[s] %-strings%] [ex:[and ]without [part( |-)?]tag[s] %-strings%]")
                        .supplier(EffPartSelectionFilterTags::new)
                        .build()
        );
    }

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
