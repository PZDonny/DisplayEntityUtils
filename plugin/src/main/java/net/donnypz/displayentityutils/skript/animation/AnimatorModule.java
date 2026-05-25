package net.donnypz.displayentityutils.skript.animation;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.animation.conditions.CondActiveGroupAnimatorIsActive;
import net.donnypz.displayentityutils.skript.animation.conditions.CondActiveGroupIsAnimating;
import net.donnypz.displayentityutils.skript.animation.conditions.CondIsAnimatorLooping;
import net.donnypz.displayentityutils.skript.animation.conditions.CondIsInAnimationCamera;
import net.donnypz.displayentityutils.skript.animation.expressions.ExprAnimatorAnimation;
import net.donnypz.displayentityutils.skript.animation.expressions.ExprDisplayAnimator;
import net.donnypz.displayentityutils.skript.animation.effects.*;
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
