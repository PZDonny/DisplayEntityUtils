package net.donnypz.displayentityutils.listeners.entity;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.entity.Entity;

class DEUStateMythicMechanic implements ITargetedEntitySkill {
    static final String mechanicName = "deustate";
    String newState;

    private DEUStateMythicMechanic(MythicLineConfig config) {
        DisplayController controller = DisplayController.getController(config.getString("id"));
        if (controller == null) {
            return;
        }
        this.newState = config.getString("newState");
    }

    static DEUStateMythicMechanic create(MythicLineConfig config){
        DEUStateMythicMechanic mechanic = new DEUStateMythicMechanic(config);
        if (mechanic.newState == null) return null;
        return mechanic;
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        SpawnedDisplayEntityGroup group = DisplayControllerManager.getControllerGroup(entity);
        if (group != null){
            DisplayStateMachine stateMachine = group.getDisplayStateMachine();
            if (stateMachine != null){
                stateMachine.setState(newState, group);
            }
        }

        return SkillResult.SUCCESS;
    }
}
