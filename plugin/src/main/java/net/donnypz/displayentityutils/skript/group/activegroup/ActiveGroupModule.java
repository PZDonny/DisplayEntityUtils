package net.donnypz.displayentityutils.skript.group.activegroup;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.group.activegroup.elements.CondActiveGroupIsPersistent;
import net.donnypz.displayentityutils.skript.group.activegroup.sections.SecSpawnGroup;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class ActiveGroupModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                CondActiveGroupIsPersistent::register,

                SecSpawnGroup::register
        );
    }

    @Override
    public String name() {
        return "activegroup";
    }
}
