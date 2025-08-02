package net.donnypz.displayentityutils.listeners.entity.mythic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

class DEUShowGroupMythicMechanic implements ITargetedEntitySkill {
    static final String mechanicName = "deushow";

    private DEUShowGroupMythicMechanic() {}

    static DEUShowGroupMythicMechanic create(){
        return new DEUShowGroupMythicMechanic();
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()){
            return SkillResult.INVALID_TARGET;
        }

        Entity entity = data.getCaster().getEntity().getBukkitEntity();
        ActiveGroup<?> group = DisplayControllerManager.getControllerGroup(entity);
        Player p = (Player) target.getBukkitEntity();

        if (group != null && p != null){
            Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> {
                if (group instanceof PacketDisplayEntityGroup pdg){
                    pdg.showToPlayer(p, GroupSpawnedEvent.SpawnReason.DISPLAY_CONTROLLER);
                }
                else if (group instanceof SpawnedDisplayEntityGroup sg){
                    sg.showToPlayer(p);
                }
            });
        }

        return SkillResult.SUCCESS;
    }
}
