package net.donnypz.displayentityutils.skript.group.spawnsettings;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.group.spawnsettings.elements.*;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class GroupSpawnSettingsModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                ExprGroupSpawnSettings::register,

                EffGroupSpawnSettingBillboard::register,
                EffGroupSpawnSettingPersistence::register,
                EffGroupSpawnSettingVisibility::register
        );
    }



    @Override
    public String name() {
        return "group spawn settings";
    }
}
