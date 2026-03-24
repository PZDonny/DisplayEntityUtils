package net.donnypz.displayentityutils.skript.group.spawnedgroup;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.conditions.CondSpawnedGroupAllowsPersistenceOverride;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.conditions.CondSpawnedGroupIsInLoadedChunk;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.conditions.CondSpawnedGroupIsSpawned;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.effects.EffSpawnedGroupMerge;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.effects.EffSpawnedGroupUnregisterOverride;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.expressions.ExprEntitySpawnedGroupPassengers;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.expressions.ExprSpawnedGroupNearLocation;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class SpawnedGroupModule implements AddonModule {
    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                CondSpawnedGroupAllowsPersistenceOverride::register,
                CondSpawnedGroupIsInLoadedChunk::register,
                CondSpawnedGroupIsSpawned::register,

                EffSpawnedGroupMerge::register,
                EffSpawnedGroupUnregisterOverride::register,

                ExprEntitySpawnedGroupPassengers::register,
                ExprSpawnedGroupNearLocation::register

        );
    }

    @Override
    public String name() {
        return "spawnedgroup";
    }
}
