package net.donnypz.displayentityutils.skript.partfilter.effects;

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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.HashSet;
import java.util.Set;

@Name("Part Filter Items/Blocks")
@Description("Set the items/blocks that should be filtered in a partfilter")
@Examples({
            "deu filter {_partfilter} with block stone",
            "deu filter {_partfilter} without blocks tag contents of minecraft tag \"impermeable\"",
            "deu filter {_partfilter} with items tag contents of minecraft tag \"swords\"",
            "",
            "#3.4.3 and earlier",
            "set block filter of {_partfilter} with stone",
            "set block filter of {_partfilter} with tag contents of minecraft tag \"impermeable\"",
            "set item filter of {_partfilter} with tag contents of minecraft tag \"swords\""
})
@Since("2.6.2")
public class EffPartFilterItemsBlocks extends Effect {

    Expression<MultiPartSelection<?>> selection;
    Expression<?> itemTypes;
    boolean exclude;
    boolean isBlock;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffPartFilterItemsBlocks.class)
                        .addPattern("deu filter %multipartfilter% with[:out] (:block|item)[s] %itemtypes%")
                        .supplier(EffPartFilterItemsBlocks::new)
                        .build()
        );
    }

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
        return "filter partfilter's items/blocks: "+selection.toString(event, debug);
    }
}
