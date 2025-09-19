package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.jetbrains.annotations.Nullable;

@Name("Animation of Display Animator")
@Description("Get the animation set for a display animator.")
@Examples({"set {_animation} to {_displayanimator}'s animation"})
@Since("3.3.1")
public class ExprAnimatorAnimation extends SimplePropertyExpression<DisplayAnimator, SpawnedDisplayAnimation> {

    static {
        register(ExprAnimatorAnimation.class, SpawnedDisplayAnimation.class, "[deu] spawned animation", "displayanimators");
    }

    @Override
    public Class<? extends SpawnedDisplayAnimation> getReturnType() {
        return SpawnedDisplayAnimation.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayAnimation convert(DisplayAnimator animator) {
        return animator != null ? animator.getAnimation() : null;
    }

    @Override
    protected String getPropertyName() {
        return "animation";
    }

}
