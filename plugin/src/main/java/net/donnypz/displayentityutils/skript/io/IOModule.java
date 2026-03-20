package net.donnypz.displayentityutils.skript.io;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.io.effects.EffBDEModelToSpawned;
import net.donnypz.displayentityutils.skript.io.expressions.ExprSavedFromLoadMethod;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class IOModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                EffBDEModelToSpawned::register,
                ExprSavedFromLoadMethod::register
        );
    }

    @Override
    public String name() {
        return "io";
    }
}
