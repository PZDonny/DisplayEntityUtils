package net.donnypz.displayentityutils.skript.group.spawnedgroup;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.elements.*;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class SpawnedGroupModule implements AddonModule {
    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                CondSpawnedGroupAllowsPersistenceOverride::register,
                CondSpawnedGroupIsInLoadedChunk::register,
                CondSpawnedGroupIsRegistered::register,
                CondSpawnedGroupIsSpawned::register,
                CondSpawnedGroupIsVisibleByDefault::register

        );
    }

    @Override
    public String name() {
        return "spawnedgroup";
    }
}
