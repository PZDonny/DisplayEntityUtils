package net.donnypz.displayentityutils.skript.player;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.player.conditions.CondPlayerCanSeeActive;
import net.donnypz.displayentityutils.skript.player.expressions.ExprPlayerSelectedAnimation;
import net.donnypz.displayentityutils.skript.player.expressions.ExprPlayerSelectedGroup;
import net.donnypz.displayentityutils.skript.player.expressions.ExprPlayerSelectedPart;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class PlayerModule implements AddonModule {

    @Override
    public void load(SkriptAddon addon) {
        SkriptUtil.registerModules(addon.syntaxRegistry(),
                CondPlayerCanSeeActive::register,

                ExprPlayerSelectedAnimation::register,
                ExprPlayerSelectedGroup::register,
                ExprPlayerSelectedPart::register
        );
    }

    @Override
    public String name() {
        return "player";
    }
}
