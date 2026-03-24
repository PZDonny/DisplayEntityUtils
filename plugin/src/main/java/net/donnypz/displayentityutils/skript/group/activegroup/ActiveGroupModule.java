package net.donnypz.displayentityutils.skript.group.activegroup;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.group.activegroup.conditions.CondActiveGroupIsPersistent;
import net.donnypz.displayentityutils.skript.group.activegroup.conditions.CondActiveGroupIsRegistered;
import net.donnypz.displayentityutils.skript.group.activegroup.conditions.CondActiveGroupIsVisibleByDefault;
import net.donnypz.displayentityutils.skript.group.activegroup.effects.*;
import net.donnypz.displayentityutils.skript.group.activegroup.expressions.*;
import net.donnypz.displayentityutils.skript.group.activegroup.sections.SecSpawnGroup;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class ActiveGroupModule implements AddonModule {

    @Override
    public void load(SkriptAddon skriptAddon) {
        SkriptUtil.registerModules(skriptAddon.syntaxRegistry(),
                CondActiveGroupIsPersistent::register,
                CondActiveGroupIsRegistered::register,
                CondActiveGroupIsVisibleByDefault::register,

                EffActiveGroupClone::register,
                EffActiveGroupDismount::register,
                EffActiveGroupPersistence::register,
                EffActiveGroupRespectEntity::register,
                EffActiveGroupRespectStop::register,
                EffActiveGroupRideEntity::register,
                EffActiveGroupScale::register,
                EffActiveGroupSetFrame::register,
                EffActiveGroupTeleport::register,
                EffActiveGroupUnregister::register,

                ExprActiveGetGroup::register,
                ExprActiveGroupCreatePartFilter::register,
                ExprActiveGroupFromPlaced::register,
                ExprActiveGroupLocation::register,
                ExprActiveGroupRideOffset::register,
                ExprActiveGroupToSaved::register,

                SecSpawnGroup::register
        );
    }

    @Override
    public String name() {
        return "activegroup";
    }
}
