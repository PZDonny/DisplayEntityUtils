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
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Interaction Part's Height/Width")
@Description("Get/Set the height or width of an interaction part.")
@Examples({"if {_activepart}'s part type is deu_interaction:",
        "\tset {_activepart}'s deu interaction height to 2"
})
@Since("3.5.0")
public class ExprInteractionDimensions extends SimplePropertyExpression<Object, Number> {

    boolean isHeight;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprInteractionDimensions.class, Number.class)
                        .addPatterns(getPatterns("deu interaction (:height|width)", "activeparts/entities"))
                        .supplier(ExprInteractionDimensions::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult){
        isHeight = parseResult.hasTag("height");
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public Class<Number> getReturnType() {
        return Number.class;
    }

    @Override
    @Nullable
    public Number convert(Object obj) {
        if (obj instanceof Interaction i){
            return isHeight ? i.getInteractionHeight() : i.getInteractionWidth();
        }
        else if (obj instanceof ActivePart p){
            return isHeight ? p.getInteractionHeight() : p.getInteractionWidth();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "part's interaction "+(isHeight ? "height" : "width");
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (delta == null) return;

        float dim = ((Number) delta[0]).floatValue();
        if (mode == Changer.ChangeMode.RESET) dim = 1.0f;


        for (Object obj : getExpr().getArray(event)){
            if (obj instanceof Interaction i){
                if (isHeight){
                    i.setInteractionHeight(dim);
                }
                else{
                    i.setInteractionWidth(dim);
                }
            }
            else if (obj instanceof ActivePart part) {
                if (isHeight){
                    part.setInteractionHeight(dim);
                }
                else{
                    part.setInteractionWidth(dim);
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
