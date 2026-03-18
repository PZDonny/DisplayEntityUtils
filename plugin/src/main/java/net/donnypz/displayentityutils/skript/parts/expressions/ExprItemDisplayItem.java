package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.aliases.ItemType;
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
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Item Display Part's Item")
@Description("Set the item of a item display part.")
@Examples({"if {_activepart}'s part type is item_display:",
        "\tset {_activepart}'s deu item to diamond_sword",
        "",
        "if {_activepart}'s part type is item_display:",
        "\tset {_activepart}'s deu item display item to stick"
})
@Since("3.5.0")
public class ExprItemDisplayItem extends SimplePropertyExpression<Object, ItemStack> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprItemDisplayItem.class, ItemStack.class)
                        .addPatterns(getPatterns("deu item [display] [item]", "activeparts/displays"))
                        .supplier(ExprItemDisplayItem::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult){
        super.init(expressions, matchedPattern, isDelayed, parseResult);
        return true;
    }

    @Override
    public Class<ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    @Nullable
    public ItemStack convert(Object obj) {
        if (obj instanceof ItemDisplay id){
            return id.getItemStack();
        }
        else if (obj instanceof ActivePart p){
            return p.getItemDisplayItem();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "part's item display item";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (delta == null) return;

        ItemStack item;
        Object obj = delta[0];
        if (mode == Changer.ChangeMode.RESET){
            item = new ItemStack(Material.AIR);
        }
        else{
            if (obj instanceof ItemStack i){
                item = i;
            }
            else if (obj instanceof BlockData bd){
                item = new ItemStack(bd.getMaterial());
            }
            else{
                item = new ItemStack(((ItemType) obj).getMaterial());
            }
        }

        for (Object o : getExpr().getArray(event)){
            if (o instanceof ItemDisplay id){
                id.setItemStack(item);
            }
            else if (o instanceof ActivePart part) {
                part.setItemDisplayItem(item);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(ItemStack.class);
        }
        return null;
    }
}
