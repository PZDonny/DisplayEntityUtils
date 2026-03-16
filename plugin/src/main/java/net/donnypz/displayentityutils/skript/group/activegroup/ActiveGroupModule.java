package net.donnypz.displayentityutils.skript.group.activegroup;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.group.activegroup.elements.CondActiveGroupAnimatorIsActive;
import net.donnypz.displayentityutils.skript.group.activegroup.elements.CondActiveGroupIsAnimating;
import net.donnypz.displayentityutils.skript.group.activegroup.elements.CondActiveGroupIsPersistent;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class ActiveGroupModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                CondActiveGroupAnimatorIsActive::register,
                CondActiveGroupIsAnimating::register,
                CondActiveGroupIsPersistent::register
        );
    }

    @Override
    public String name() {
        return "activegroup";
    }
}
