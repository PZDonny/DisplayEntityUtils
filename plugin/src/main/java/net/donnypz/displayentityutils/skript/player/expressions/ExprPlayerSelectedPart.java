package net.donnypz.displayentityutils.skript.player.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Player's Selected Part")
@Description("Get the selected active part of a player")
@Examples({
        "set {_activepart} to player's selected part"
})
@Since("3.4.3")
public class ExprPlayerSelectedPart extends SimplePropertyExpression<Player, ActivePart> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprPlayerSelectedPart.class, ActivePart.class)
                        .addPatterns(getPatterns("selected [active] part", "players"))
                        .supplier(ExprPlayerSelectedPart::new)
                        .build()
        );
    }

    @Override
    public Class<? extends ActivePart> getReturnType() {
        return ActivePart.class;
    }

    @Override
    @Nullable
    public ActivePart convert(Player player) {;
        if (player != null){
            ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
            if (sel == null) return null;
            return sel.getSelectedPart();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "selected active part";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
