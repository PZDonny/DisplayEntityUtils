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
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Text Display Part's Line Width")
@Description("Set the line width of a text display part.")
@Examples({"if {_activepart}'s part type is text_display:",
        "\tset {_activepart}'s deu text line width to 50"
})
@Since("3.5.0, 3.5.2 (Text Displays Entities)")
public class ExprTextDisplayLineWidth extends SimplePropertyExpression<Object, Number> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprTextDisplayLineWidth.class, Number.class)
                        .addPatterns(getPatterns("deu text [display] line width", "activeparts/displays"))
                        .supplier(ExprTextDisplayLineWidth::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult){
        super.init(expressions, matchedPattern, isDelayed, parseResult);
        return true;
    }

    @Override
    public Class<Number> getReturnType() {
        return Number.class;
    }

    @Override
    @Nullable
    public Number convert(Object obj) {
        if (obj instanceof ActivePart part){
            return part.getTextDisplayLineWidth();
        }
        else if (obj instanceof TextDisplay td){
            return td.getLineWidth();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "part's text display line width";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (delta == null) return;

        int width = mode == Changer.ChangeMode.RESET ? 200 : ((Number) delta[0]).intValue();

        for (Object o : getExpr().getArray(event)){
            if (o instanceof ActivePart part) {
                part.setTextDisplayLineWidth(width);
            }
            else if (o instanceof TextDisplay td){
                td.setLineWidth(width);
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
