package net.donnypz.displayentityutils.skript.animation.framepoints;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.animation.framepoints.expressions.ExprFramePointFromFrame;
import net.donnypz.displayentityutils.skript.animation.framepoints.expressions.ExprFramePointRelativeLocation;
import net.donnypz.displayentityutils.skript.animation.framepoints.expressions.ExprFramePointsFromFrame;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class FramePointsModule implements AddonModule {

    @Override
    public void load(SkriptAddon addon) {
        SkriptUtil.registerModules(addon.syntaxRegistry(),
                ExprFramePointFromFrame::register,
                ExprFramePointRelativeLocation::register,
                ExprFramePointsFromFrame::register
        );
    }

    @Override
    public String name() {
        return "framepoints";
    }
}
