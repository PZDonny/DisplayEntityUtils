package net.donnypz.displayentityutils.skript.general;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.general.elements.CondHasTag;
import net.donnypz.displayentityutils.skript.general.elements.ExprTag;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class GeneralModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                CondHasTag::register,
                ExprTag::register
        );
    }

    @Override
    public String name() {
        return "";
    }
}
