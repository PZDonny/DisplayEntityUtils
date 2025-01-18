package net.donnypz.displayentityutils.listeners.entity;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.entity.Entity;

class DEUStopMythicMechanic implements ITargetedEntitySkill {
    static final String mechanicName = "deustop";

    private DEUStopMythicMechanic() {}

    static DEUStopMythicMechanic create(){
        return new DEUStopMythicMechanic();
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        SpawnedDisplayEntityGroup group = DisplayControllerManager.getControllerGroup(entity);
        if (group != null){
            group.unsetMachineState();
        }

        return SkillResult.SUCCESS;
    }
}
