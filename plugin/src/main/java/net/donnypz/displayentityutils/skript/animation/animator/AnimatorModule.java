package net.donnypz.displayentityutils.skript.animation.animator;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.animation.animator.conditions.CondActiveGroupAnimatorIsActive;
import net.donnypz.displayentityutils.skript.animation.animator.conditions.CondActiveGroupIsAnimating;
import net.donnypz.displayentityutils.skript.animation.animator.conditions.CondIsAnimatorLooping;
import net.donnypz.displayentityutils.skript.animation.animator.conditions.CondIsInAnimationCamera;
import net.donnypz.displayentityutils.skript.animation.animator.effects.*;
import net.donnypz.displayentityutils.skript.animation.animator.expressions.ExprAnimatorAnimation;
import net.donnypz.displayentityutils.skript.animation.animator.expressions.ExprDisplayAnimator;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class AnimatorModule implements AddonModule {

    @Override
    public void load(SkriptAddon addon) {
        SkriptUtil.registerModules(addon.syntaxRegistry(),
                CondActiveGroupAnimatorIsActive::register,
                CondActiveGroupIsAnimating::register,
                CondIsAnimatorLooping::register,
                CondIsInAnimationCamera::register,

                EffActiveGroupPlayAnimation::register,
                EffActiveGroupPlayAnimationCamera::register,
                EffActiveGroupStopAllAnimations::register,
                EffActiveGroupStopAnimation::register,
                EffActiveGroupStopAnimationCamera::register,
                EffPlayerStopAllPacketAnimations::register,

                ExprAnimatorAnimation::register,
                ExprDisplayAnimator::register
        );
    }

    @Override
    public String name() {
        return "animator";
    }
}
