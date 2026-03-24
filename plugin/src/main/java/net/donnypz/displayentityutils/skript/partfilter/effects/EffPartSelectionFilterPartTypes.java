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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.Arrays;

@Name("Part Filter Part Types")
@Description("Set the part types of parts that should be filtered in a partfilter")
@Examples({
        "deu filter {_partfilter} with type item_display",
        "deu filter {_partfilter} without types deu_interaction and text_display",
        "",
        "#3.4.3 and earlier",
        "set part type filter of {_partfilter} to text_display",
        "set type filter of {_partfilter} to exclude block_display and item_display"})
@Since("2.6.2")
public class EffPartSelectionFilterPartTypes extends Effect {

    Expression<MultiPartSelection> selection;
    Expression<SpawnedDisplayEntityPart.PartType> partTypes;
    boolean exclude;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffPartSelectionFilterPartTypes.class)
                        .addPattern("deu filter %partfilter% with[:out] type[s] %parttypes%")
                        .supplier(EffPartSelectionFilterPartTypes::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        selection = (Expression<MultiPartSelection>) expressions[0];
        partTypes = (Expression<SpawnedDisplayEntityPart.PartType>) expressions[1];
        exclude = parseResult.hasTag("out");
        return true;
    }

    @Override
    protected void execute(Event event) {
        MultiPartSelection sel = selection.getSingle(event);
        if (sel == null){
            return;
        }
        PartFilter builder = new PartFilter();
        SpawnedDisplayEntityPart.PartType[] types = partTypes.getArray(event);
        if (types == null){
            return;
        }

        if (exclude){
            ArrayList<SpawnedDisplayEntityPart.PartType> list = new ArrayList<>(Arrays.stream(SpawnedDisplayEntityPart.PartType.values()).toList());
            for (SpawnedDisplayEntityPart.PartType type : types){
                list.remove(type);
            }
            builder.setPartTypes(list);
        }
        else{
            builder.setPartTypes(types);
        }

        sel.applyFilter(builder, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set part filter part types: "+selection.toString(event, debug);
    }
}