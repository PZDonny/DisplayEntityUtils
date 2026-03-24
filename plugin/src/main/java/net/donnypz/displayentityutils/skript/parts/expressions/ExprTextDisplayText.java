package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Utils;
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
@Since("3.5.0")
public class ExprTextDisplayText extends SimplePropertyExpression<ActivePart, String> {

    private static final BungeeComponentSerializer SERIALIZER = BungeeComponentSerializer.get();

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprTextDisplayText.class, String.class)
                        .addPatterns(getPatterns("deu text [display] [text]", "activeparts"))
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
    public Class<String> getReturnType() {
        return String.class;
    }

    @Override
    @Nullable
    public String convert(ActivePart part) {
        Component comp = part.getTextDisplayText();
        return comp != null ? Utils.replaceChatStyles(LegacyComponentSerializer.legacySection().serialize(comp)) : null;
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
        String value = delta == null ? null : String.join("\n", Arrays.copyOf(delta, delta.length, String[].class));
        final Component finalComp;
        if (SERIALIZER != null && value != null) {
            finalComp = SERIALIZER.deserialize(BungeeConverter.convert(ChatMessages.parseToArray(value)));
        }
        else{
            finalComp = null;
        }
        if (finalComp == null) return;

        for (Object object : getExpr().getArray(event)) {
            if (object instanceof ActivePart ap){
                ap.setTextDisplayText(finalComp);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String[].class);
        }
        return null;
    }
}
