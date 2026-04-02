package net.donnypz.displayentityutils.skript.io;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.io.expressions.ExprSavedFromLoadMethod;
import net.donnypz.displayentityutils.skript.io.sections.SecSpawnBDEModel;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class IOModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                ExprSavedFromLoadMethod::register,
                SecSpawnBDEModel::register
        );
    }

    @Override
    public String name() {
        return "io";
    }
}
