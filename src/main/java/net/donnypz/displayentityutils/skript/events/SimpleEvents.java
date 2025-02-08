package net.donnypz.displayentityutils.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class SimpleEvents extends SimpleEvent {

    static{
        if (DisplayEntityPlugin.isSkriptInstalled()){

            //Group Registered Event
            Skript.registerEvent("Group Registered", SimpleEvents.class, GroupRegisteredEvent.class, "[spawned] [display] group register[ed]")
                    .description("Called when a spawned group is registered by nearby selection or when spawned from a saved group")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupRegisteredEvent.class, SpawnedDisplayEntityGroup.class, GroupRegisteredEvent::getGroup);

            //Chunk Register Group Event
            Skript.registerEvent("Chunk Register Group", SimpleEvents.class, ChunkRegisterGroupEvent.class, "chunk register[ing] [spawned] [display] group")
                    .description("Called when a chunk loads a spawned group and registers it")
                    .since("2.6.2");
            EventValues.registerEventValue(ChunkRegisterGroupEvent.class, SpawnedDisplayEntityGroup.class, ChunkRegisterGroupEvent::getGroup);
            EventValues.registerEventValue(ChunkRegisterGroupEvent.class, Chunk.class, ChunkRegisterGroupEvent::getChunk);

            //Group Unregistered Event
            Skript.registerEvent("Group Unregistered", SimpleEvents.class, GroupUnregisteredEvent.class, "[spawned] [display] group unregister[ed]")
                    .description("Called when a spawned group is unregistered")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupUnregisteredEvent.class, SpawnedDisplayEntityGroup.class, GroupUnregisteredEvent::getGroup);

            //Pre Group Spawned Event
            Skript.registerEvent("Pre Group Spawned", SimpleEvents.class, PreGroupSpawnedEvent.class, "pre [spawned] [display] group spawn[ed]")
                    .description("Called before a spawned group is spawned and created")
                    .since("2.6.2");
            EventValues.registerEventValue(PreGroupSpawnedEvent.class, DisplayEntityGroup.class, PreGroupSpawnedEvent::getGroup);
            EventValues.registerEventValue(PreGroupSpawnedEvent.class, GroupSpawnedEvent.SpawnReason.class, PreGroupSpawnedEvent::getSpawnReason);
            EventValues.registerEventValue(PreGroupSpawnedEvent.class, GroupSpawnSettings.class, PreGroupSpawnedEvent::getNewSettings);

            //Group Spawned Event
            Skript.registerEvent("Group Spawned", SimpleEvents.class, GroupSpawnedEvent.class, "[spawned] [display] group spawn[ed]")
                    .description("Called when a spawned group is spawned from a saved group")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupSpawnedEvent.class, SpawnedDisplayEntityGroup.class, GroupSpawnedEvent::getGroup);
            EventValues.registerEventValue(GroupSpawnedEvent.class, GroupSpawnedEvent.SpawnReason.class, GroupSpawnedEvent::getSpawnReason);

            //Group Animation Start Event
            Skript.registerEvent("Group Animation Start", SimpleEvents.class, GroupAnimationStartEvent.class, "[spawned] [display] anim[ation] frame (complete[d]|end[ed])")
                    .description("Called when a display animator starts playing a spawned animation")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupAnimationStartEvent.class, SpawnedDisplayEntityGroup.class, GroupAnimationStartEvent::getGroup);
            EventValues.registerEventValue(GroupAnimationStartEvent.class, SpawnedDisplayAnimation.class, GroupAnimationStartEvent::getAnimation);
            EventValues.registerEventValue(GroupAnimationStartEvent.class, DisplayAnimator.class, GroupAnimationStartEvent::getAnimator);

            //Group Animation Loop Start Event
            Skript.registerEvent("Group Animation Loop Start", SimpleEvents.class, GroupAnimationLoopStartEvent.class, "[spawned] [display] anim[ation] loop start[ed]")
                    .description("Called when a display animator begins a new animation loop")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupAnimationLoopStartEvent.class, SpawnedDisplayEntityGroup.class, GroupAnimationLoopStartEvent::getGroup);
            EventValues.registerEventValue(GroupAnimationLoopStartEvent.class, SpawnedDisplayAnimation.class, GroupAnimationLoopStartEvent::getAnimation);
            EventValues.registerEventValue(GroupAnimationLoopStartEvent.class, DisplayAnimator.class, GroupAnimationLoopStartEvent::getAnimator);

            //Group Animate Frame Start Event
            Skript.registerEvent("Group Animate Frame Start", SimpleEvents.class, GroupAnimateFrameStartEvent.class, "[spawned] [display] anim[ation] frame start[ed]")
                    .description("Called when a frame beings animating on a spawned group")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupAnimateFrameStartEvent.class, SpawnedDisplayEntityGroup.class, GroupAnimateFrameStartEvent::getGroup);
            EventValues.registerEventValue(GroupAnimateFrameStartEvent.class, SpawnedDisplayAnimation.class, GroupAnimateFrameStartEvent::getAnimation);
            EventValues.registerEventValue(GroupAnimateFrameStartEvent.class, SpawnedDisplayAnimationFrame.class, GroupAnimateFrameStartEvent::getFrame);
            EventValues.registerEventValue(GroupAnimateFrameStartEvent.class, DisplayAnimator.class, GroupAnimateFrameStartEvent::getAnimator);

            //Group Animate Frame End Event
            Skript.registerEvent("Group Animate Frame End", SimpleEvents.class, GroupAnimateFrameEndEvent.class, "[spawned] [display] anim[ation] frame (complete[d]|end[ed])")
                    .description("Called when a frame ends animating on a spawned group.",
                            "Ignores frame delay and is called after translation of parts")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupAnimateFrameEndEvent.class, SpawnedDisplayEntityGroup.class, GroupAnimateFrameEndEvent::getGroup);
            EventValues.registerEventValue(GroupAnimateFrameEndEvent.class, SpawnedDisplayAnimation.class, GroupAnimateFrameEndEvent::getAnimation);
            EventValues.registerEventValue(GroupAnimateFrameEndEvent.class, SpawnedDisplayAnimationFrame.class, GroupAnimateFrameEndEvent::getFrame);
            EventValues.registerEventValue(GroupAnimateFrameEndEvent.class, DisplayAnimator.class, GroupAnimateFrameEndEvent::getAnimator);

            //Group Animation Complete Event
            Skript.registerEvent("Group Animation Complete", SimpleEvents.class, GroupAnimationCompleteEvent.class, "[spawned] [display] anim[ation] (complete[d]|end[ed])")
                    .description("Called at the completion of a spawned animation. This is not called for looping animations/animators")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupAnimationCompleteEvent.class, SpawnedDisplayEntityGroup.class, GroupAnimationCompleteEvent::getGroup);
            EventValues.registerEventValue(GroupAnimationCompleteEvent.class, SpawnedDisplayAnimation.class, GroupAnimationCompleteEvent::getAnimation);
            EventValues.registerEventValue(GroupAnimationCompleteEvent.class, DisplayAnimator.class, GroupAnimationCompleteEvent::getAnimator);

            //Pre Interaction Click Event
            Skript.registerEvent("Pre Interaction Click", SimpleEvents.class, PreInteractionClickEvent.class, "pre interaction [entity] click[ed]")
                    .description("Called when an interaction entity is clicked before the Interaction Click Event, but does not load commands")
                    .since("2.6.2");
            EventValues.registerEventValue(PreInteractionClickEvent.class, Player.class, e -> e.getPlayer());
            EventValues.registerEventValue(PreInteractionClickEvent.class, Interaction.class, e -> e.getInteraction());
            EventValues.registerEventValue(PreInteractionClickEvent.class, InteractionClickEvent.ClickType.class, e -> e.getClickType());

            //Interaction Click Event
            Skript.registerEvent("Interaction Click", SimpleEvents.class, InteractionClickEvent.class, "interaction [entity] click[ed]")
                    .description("Called when an interaction entity is clicked, providing information stored on the interaction entity")
                    .since("2.6.2");
            EventValues.registerEventValue(InteractionClickEvent.class, Player.class, e -> e.getPlayer());
            EventValues.registerEventValue(InteractionClickEvent.class, Interaction.class, e -> e.getInteraction());
            EventValues.registerEventValue(InteractionClickEvent.class, InteractionClickEvent.ClickType.class, e -> e.getClickType());
            EventValues.registerEventValue(InteractionClickEvent.class, List.class, InteractionClickEvent::getCommands);

            //Entity Ride Group Event
            Skript.registerEvent("Entity Ride Group", SimpleEvents.class, EntityRideGroupEvent.class, "entity (mount|ride) [spawned[-| ]]group")
                    .description("Called when an entity is mounted on a spawned group")
                    .since("2.6.2");
            EventValues.registerEventValue(EntityRideGroupEvent.class, SpawnedDisplayEntityGroup.class, EntityRideGroupEvent::getGroup);
            EventValues.registerEventValue(EntityRideGroupEvent.class, Entity.class, EntityRideGroupEvent::getEntity);

            //Group Ride Entity Event
            Skript.registerEvent("Group Ride Entity", SimpleEvents.class, GroupRideEntityEvent.class, "[spawned[-| ]]group (mount|ride) entity")
                    .description("Called when a spawned group is mounted on an entity")
                    .since("2.6.2");
            EventValues.registerEventValue(GroupRideEntityEvent.class, SpawnedDisplayEntityGroup.class, GroupRideEntityEvent::getGroup);
            EventValues.registerEventValue(GroupRideEntityEvent.class, Entity.class, GroupRideEntityEvent::getEntity);

        }
    }
}
