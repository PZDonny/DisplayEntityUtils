package net.donnypz.displayentityutils.skript.player.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Player's Selected Animation")
@Description("Get/Set the selected animation of a player")
@Examples({
        "#Get selected animation",
        "set {_animation} to player's selected animation",
        "",
        "#Set selected animation",
        "set player's selected anim to {_animation}",
        "",
        "#Reset selected animation",
        "reset player's selected animation"
        })
@Since("2.6.3, 3.3.2 (Plural)")
public class ExprPlayerSelectedAnimation extends SimplePropertyExpression<Player, SpawnedDisplayAnimation> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprPlayerSelectedAnimation.class, SpawnedDisplayAnimation.class)
                        .addPatterns(getPatterns("selected anim[ation]", "players"))
                        .supplier(ExprPlayerSelectedAnimation::new)
                        .build()
        );
    }

    @Override
    public Class<? extends SpawnedDisplayAnimation> getReturnType() {
        return SpawnedDisplayAnimation.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayAnimation convert(Player player) {;
        if (player != null){
            return DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "selected animation";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Player p = getExpr().getSingle(event);
        if (p == null){
            return;
        }

        switch (mode) {
            case SET -> {
                if (delta == null){
                    return;
                }
                DisplayAnimationManager.setSelectedSpawnedAnimation(p.getPlayer(), (SpawnedDisplayAnimation) delta[0]);
            }
            case RESET -> DisplayAnimationManager.deselectSpawnedAnimation(p);
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(SpawnedDisplayAnimation.class);
        }
        return null;
    }
}
