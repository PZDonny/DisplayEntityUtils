package net.donnypz.displayentityutils.skript.active;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.active.conditions.CondActiveIsPacketBased;
import net.donnypz.displayentityutils.skript.active.effects.*;
import net.donnypz.displayentityutils.skript.active.expressions.*;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class ActiveModule implements AddonModule {

    @Override
    public void load(SkriptAddon addon) {
        SkriptUtil.registerModules(addon.syntaxRegistry(),
                CondActiveIsPacketBased::register,

                EffActiveGlow::register,
                EffActiveRotate::register,
                EffActiveTranslate::register,

                ExprActiveBillboard::register,
                ExprActiveDirection::register,
                ExprActiveGlowColor::register,
                ExprActiveInterpolation::register,
                ExprActiveTeleportDuration::register,
                ExprActiveViewRange::register
        );
    }

    @Override
    public String name() {
        return "";
    }
}
