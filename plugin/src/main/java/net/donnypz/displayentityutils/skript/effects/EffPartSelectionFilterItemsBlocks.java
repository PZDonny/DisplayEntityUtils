package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
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
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Name("Part Selection Filter Set Items/Blocks")
@Description("Set the items/blocks that should be filtered in a part selection")
@Examples({"#Filter Stone",
            "set block filter of {_selection} with stone",
            "",
            "#Filter Glass and Stained Glass",
            "set block filter of {_selection} with tag contents of minecraft tag \"impermeable\"",
            "#Filter All Swords",
            "set item filter of {_selection} with tag contents of minecraft tag \"swords\""})
@Since("2.6.2")
public class EffPartSelectionFilterItemsBlocks extends Effect {
    static {
        Skript.registerEffect(EffPartSelectionFilterItemsBlocks.class,"set (:block|item)[s] filter (for|of) %multipartselection% with[:out] %itemtypes%");
    }

    Expression<MultiPartSelection<?>> selection;
    Expression<?> itemTypes;
    boolean exclude;
    boolean isBlock;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        selection = (Expression<MultiPartSelection<?>>) expressions[0];
        itemTypes = expressions[1];
        exclude = parseResult.hasTag("out");
        isBlock = parseResult.hasTag("block");
        return true;
    }

    @Override
    protected void execute(Event event) {
        MultiPartSelection<?> sel = selection.getSingle(event);
        if (sel == null){
            return;
        }
        ItemType[] types = (ItemType[]) itemTypes.getAll(event);
        if (types == null){
            return;
        }

        Set<Material> materials = new HashSet<>();
        for (ItemType type : types){
            for (Material mat : type.getMaterials()){
                materials.add(mat);
            }
        }

        PartFilter filter = new PartFilter();
        if (isBlock){
            filter.setBlockTypes(materials, !exclude);
        }
        else{
            filter.setItemTypes(materials, !exclude);
        }

        sel.applyFilter(filter, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set part selection items/blocks: "+selection.toString(event, debug);
    }
}
