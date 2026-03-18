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
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Item Display Part's Transform")
@Description("Set the item display transform of a part, if it's of a item display type.")
@Examples({"if {_activepart}'s part type is item_display:",
        "\tset {_activepart}'s deu item transform to first person left handed"
})
@Since("3.5.0")
public class ExprItemDisplayTransform extends SimplePropertyExpression<Object, ItemDisplay.ItemDisplayTransform> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprItemDisplayTransform.class, ItemDisplay.ItemDisplayTransform.class)
                        .addPatterns(getPatterns("deu item [display] transform", "activeparts/displays"))
                        .supplier(ExprItemDisplayTransform::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult){
        super.init(expressions, matchedPattern, isDelayed, parseResult);
        return true;
    }

    @Override
    public Class<ItemDisplay.ItemDisplayTransform> getReturnType() {
        return ItemDisplay.ItemDisplayTransform.class;
    }

    @Override
    @Nullable
    public ItemDisplay.ItemDisplayTransform convert(Object obj) {
        if (obj instanceof ItemDisplay id){
            return id.getItemDisplayTransform();
        }
        else if (obj instanceof ActivePart p){
            return p.getItemDisplayTransform();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "part's item display transform";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (delta == null) return;

        ItemDisplay.ItemDisplayTransform transform = mode == Changer.ChangeMode.RESET ?
                ItemDisplay.ItemDisplayTransform.NONE
                :
                ((ItemDisplay.ItemDisplayTransform) delta[0]);
        for (Object obj : getExpr().getArray(event)){
            if (obj instanceof ItemDisplay id){
                id.setItemDisplayTransform(transform);
            }
            else if (obj instanceof ActivePart part) {
                part.setItemDisplayTransform(transform);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(ItemDisplay.ItemDisplayTransform.class);
        }
        return null;
    }
}
