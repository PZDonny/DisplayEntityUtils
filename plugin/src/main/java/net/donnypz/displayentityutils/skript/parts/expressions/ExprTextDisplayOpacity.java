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

@Name("Text Display Part's Opacity")
@Description("Set the opacity of a text display part.")
@Examples({"if {_activepart}'s part type is text_display:",
        "\tset {_activepart}'s deu opacity to 105"
})
@Since("3.5.0, 3.5.2 (Text Displays Entities)")
public class ExprTextDisplayOpacity extends SimplePropertyExpression<Object, Number> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprTextDisplayOpacity.class, Number.class)
                        .addPatterns(getPatterns("deu [text] [display] opacity", "activeparts/displays"))
                        .supplier(ExprTextDisplayOpacity::new)
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
            return part.getTextDisplayTextOpacity();
        }
        else if (obj instanceof TextDisplay td){
            return td.getTextOpacity();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "deu opacity";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, RESET, SET -> CollectionUtils.array(Number.class);
            default -> null;
        };
    }


    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Object[] parts = getExpr().getArray(event);
        int change = delta == null ? 255 : ((Number) delta[0]).intValue();
        switch (mode) {
            case REMOVE_ALL:
            case REMOVE:
                change = -change;
                //$FALL-THROUGH$
            case ADD:
                for (Object obj : parts) {
                    if (obj instanceof ActivePart part){
                        byte value = convertToSigned(Math.clamp(convertToUnsigned(part.getTextDisplayTextOpacity()) + change, 0, 255));
                        part.setTextDisplayTextOpacity(value);
                    }
                    else if (obj instanceof TextDisplay td){
                        byte value = convertToSigned(Math.clamp(convertToUnsigned(td.getTextOpacity()) + change, 0, 255));
                        td.setTextOpacity(value);
                    }
                }
                break;
            case DELETE:
            case RESET:
            case SET:
                change = convertToSigned(Math.clamp(change, -128, 255));
                for (Object obj : parts) {
                    if (obj instanceof ActivePart part){
                        part.setTextDisplayTextOpacity((byte) change);
                    }
                    else if (obj instanceof TextDisplay td){
                        td.setTextOpacity((byte) change);
                    }
                }
                break;
        }
    }

    private static int convertToUnsigned(byte value) {
        return value < 0 ? 256 + value : value;
    }

    private static byte convertToSigned(int value) {
        if (value > 127)
            value -= 256;
        return (byte) value;
    }
}
