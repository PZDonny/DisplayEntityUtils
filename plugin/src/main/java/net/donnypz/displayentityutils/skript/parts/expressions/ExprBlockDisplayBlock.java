package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Block Display Part's Block")
@Description("Set the block of a block display part.")
@Examples({"if {_activepart}'s part type is block_display:",
        "\tset {_activepart}'s deu block to stone"
})
@Since("3.5.0")
public class ExprBlockDisplayBlock extends SimplePropertyExpression<Object, BlockData> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprBlockDisplayBlock.class, BlockData.class)
                        .addPatterns(getPatterns("deu block [data]", "activeparts/displays"))
                        .supplier(ExprBlockDisplayBlock::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult){
        super.init(expressions, matchedPattern, isDelayed, parseResult);
        return true;
    }

    @Override
    public Class<BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    @Nullable
    public BlockData convert(Object obj) {
        if (obj instanceof BlockDisplay bd){
            return bd.getBlock();
        }
        else if (obj instanceof ActivePart p){
            return p.getBlockDisplayBlock();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "part's block display block";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (delta == null) return;
        BlockData blockData = ((BlockData) delta[0]);
        for (Object obj : getExpr().getArray(event)){
            if (obj instanceof BlockDisplay bd){
                bd.setBlock(blockData);
            }
            else if (obj instanceof ActivePart part) {
                part.setBlockDisplayBlock(blockData);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(BlockData.class);
        }
        return null;
    }
}
