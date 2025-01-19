package net.donnypz.displayentityutils.listeners.entity;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.MachineState;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.entity.Entity;

import java.util.UUID;

class DEUAnimationMythicMechanic implements ITargetedEntitySkill {
    static final String mechanicName = "deuanimate";
    MachineState state;
    final String id = UUID.randomUUID().toString();

    private DEUAnimationMythicMechanic(MythicLineConfig config) {
        DisplayController controller = DisplayController.getController(config.getString("id"));
        if (controller == null) {
            return;
        }
        String animTag = config.getString("anim");


        DisplayAnimator.AnimationType type;
        try{
            type = DisplayAnimator.AnimationType.valueOf(config.getString("t", "type"));
        }
        catch(IllegalArgumentException e){
            type = DisplayAnimator.AnimationType.LINEAR;
        }

        LoadMethod loadMethod;
        try{
            loadMethod = LoadMethod.valueOf(config.getString("s","storage"));
        }
        catch(IllegalArgumentException e){
            loadMethod = null;
        }

        state = new MachineState(controller.getStateMachine(), id, animTag, loadMethod, type, true);
        state.ignoreOtherTransitionLocks();
        state.skillState();
    }

    static DEUAnimationMythicMechanic create(MythicLineConfig config){
        DEUAnimationMythicMechanic mechanic = new DEUAnimationMythicMechanic(config);
        if (mechanic.state == null){
            return null;
        }
        return mechanic;
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        SpawnedDisplayEntityGroup group = DisplayControllerManager.getControllerGroup(entity);
        if (group != null){
            group.setMachineState(state, state.getStateMachine());
        }

        return SkillResult.SUCCESS;
    }
}
