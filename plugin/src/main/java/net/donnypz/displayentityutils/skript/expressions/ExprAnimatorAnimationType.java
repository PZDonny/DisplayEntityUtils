package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import org.jetbrains.annotations.Nullable;

@Name("Animation Type of Display Animator")
@Description("Get the animation type of a display animator.")
@Examples({"set {_animtype} to {_displayanimator}'s animation type"})
@Since("3.3.6")
public class ExprAnimatorAnimationType extends SimplePropertyExpression<DisplayAnimator, DisplayAnimator.AnimationType> {

    static {
        register(ExprAnimatorAnimationType.class, DisplayAnimator.AnimationType.class, "[deu] anim[ation] type", "displayanimator");
    }

    @Override
    public Class<? extends DisplayAnimator.AnimationType> getReturnType() {
        return DisplayAnimator.AnimationType.class;
    }

    @Override
    @Nullable
    public DisplayAnimator.AnimationType convert(DisplayAnimator animator) {
        return animator != null ? animator.getAnimationType() : null;
    }

    @Override
    protected String getPropertyName() {
        return "animation type";
    }

}
