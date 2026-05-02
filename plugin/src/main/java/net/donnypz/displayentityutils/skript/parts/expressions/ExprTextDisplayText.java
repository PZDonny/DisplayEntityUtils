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
import net.kyori.adventure.text.Component;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;

@Name("Text Display Part's Text")
@Description("Set the text of a text display part.")
@Examples({
        "if {_activepart}'s part type is text_display:",
        "\tset {_activepart}'s deu text to \"&aMy New Text\""
})
@Since("3.5.0, 3.5.2 (Text Displays Entities)")
public class ExprTextDisplayText extends SimplePropertyExpression<Object, Component> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprTextDisplayText.class, Component.class)
                        .addPatterns(getPatterns("deu text [display] [text]", "activeparts/displays"))
                        .supplier(ExprTextDisplayText::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult){
        super.init(expressions, matchedPattern, isDelayed, parseResult);
        return true;
    }

    @Override
    public Class<Component> getReturnType() {
        return Component.class;
    }

    @Override
    @Nullable
    public Component convert(Object obj) {
        if (obj instanceof TextDisplay td){
            return td.text();
        }
        else if (obj instanceof ActivePart p){
            return p.getTextDisplayText();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "part's text display text";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Component component = delta == null ? Component.empty() : joinByNewLine(Arrays.copyOf(delta, delta.length, Component[].class));

        for (Object object : getExpr().getArray(event)) {
            if (object instanceof ActivePart ap){
                ap.setTextDisplayText(component);
            }
            else if (object instanceof TextDisplay td){
                td.text(component);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        return switch (mode) {
            case RESET -> CollectionUtils.array();
            case SET -> CollectionUtils.array(Component[].class);
            default -> null;
        };
    }

    //from Skript's TextComponentUtils
    private Component joinByNewLine(Component... components) {
        // we want formatting from the first to apply to the next, so append this way
        Component combined = components[0];
        for (int i = 1; i < components.length; i++) {
            combined = combined.appendNewline().append(components[i]);
        }
        return combined.compact();
    }
}
