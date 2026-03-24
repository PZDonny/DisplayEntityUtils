package net.donnypz.displayentityutils.skript.misc;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.misc.conditions.CondHasTag;
import net.donnypz.displayentityutils.skript.misc.conditions.CondInteractionCommandType;
import net.donnypz.displayentityutils.skript.misc.expressions.ExprTag;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class MiscModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                CondHasTag::register,
                CondInteractionCommandType::register,

                ExprTag::register
        );
    }

    @Override
    public String name() {
        return "misc";
    }
}
