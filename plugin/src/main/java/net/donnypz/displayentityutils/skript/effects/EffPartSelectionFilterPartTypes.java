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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

@Name("Part Filter/Selection Set Part Types")
@Description("Set the part types that should be filtered in a part selection")
@Examples({"set part type filter of {_selection} to text_display",
        "set type filter of {_selection} to exclude block_display and item_display"})
@Since("2.6.2")
public class EffPartSelectionFilterPartTypes extends Effect {
    static {
        Skript.registerEffect(EffPartSelectionFilterPartTypes.class,"set [part] type[s] filter (for|of) %multipartfilter% (with[:out]|to [out:exclude]) %parttypes%");
    }

    Expression<MultiPartSelection> selection;
    Expression<SpawnedDisplayEntityPart.PartType> partTypes;
    boolean exclude;

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
        return "set part selection part types: "+selection.toString(event, debug);
    }
}