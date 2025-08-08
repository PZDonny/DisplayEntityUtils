package net.donnypz.displayentityutils.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

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

            //Packet Group Send Event
            Skript.registerEvent("Packet Group Send", SimpleEvents.class, PacketGroupSendEvent.class, "packet [spawned] [display] group (spawn[ed]|sent|create[d])")
                    .description("Called when a packet-based group is sent to players")
                    .since("3.0.0");
            EventValues.registerEventValue(PacketGroupSendEvent.class, PacketDisplayEntityGroup.class, PacketGroupSendEvent::getGroup);
            EventValues.registerEventValue(PacketGroupSendEvent.class, GroupSpawnedEvent.SpawnReason.class, PacketGroupSendEvent::getSpawnReason);
            EventValues.registerEventValue(PacketGroupSendEvent.class, GroupSpawnSettings.class, PacketGroupSendEvent::getNewSettings);
            EventValues.registerEventValue(PacketGroupSendEvent.class, Player[].class, e -> e.getPlayers().toArray(new Player[0]));

            //Packet Group Destroy Event
            Skript.registerEvent("Packet Group Destroyed", SimpleEvents.class, PacketGroupDestroyEvent.class, "packet [spawned] [display] group (destroy[ed]|remove[d]|hid(e|den))")
                    .description("Called when a packet-based group is hidden from players")
                    .since("3.0.0");
            EventValues.registerEventValue(PacketGroupDestroyEvent.class, PacketDisplayEntityGroup.class, PacketGroupDestroyEvent::getGroup);
            EventValues.registerEventValue(PacketGroupDestroyEvent.class, Player[].class, e -> e.getPlayers().toArray(new Player[0]));

            //Animation Start Event
            Skript.registerEvent("Animation Start", SimpleEvents.class, AnimationStartEvent.class, "[spawned] [display] anim[ation] start[ed]")
                    .description("Called when a display animator starts playing a spawned animation")
                    .since("3.2.1");
            EventValues.registerEventValue(AnimationStartEvent.class, SpawnedDisplayEntityGroup.class, AnimationStartEvent::getGroup);
            EventValues.registerEventValue(AnimationStartEvent.class, SpawnedDisplayAnimation.class, AnimationStartEvent::getAnimation);
            EventValues.registerEventValue(AnimationStartEvent.class, DisplayAnimator.class, AnimationStartEvent::getAnimator);

            //Animation Loop Start Event
            Skript.registerEvent("Animation Loop Start", SimpleEvents.class, AnimationLoopStartEvent.class, "[spawned] [display] anim[ation] loop start[ed]")
                    .description("Called when a display animator begins a new animation loop")
                    .since("2.6.2");
            EventValues.registerEventValue(AnimationLoopStartEvent.class, ActiveGroup.class, AnimationLoopStartEvent::getGroup);
            EventValues.registerEventValue(AnimationLoopStartEvent.class, SpawnedDisplayAnimation.class, AnimationLoopStartEvent::getAnimation);
            EventValues.registerEventValue(AnimationLoopStartEvent.class, DisplayAnimator.class, AnimationLoopStartEvent::getAnimator);

            //Animation Frame Start Event
            Skript.registerEvent("Animation Frame Start", SimpleEvents.class, AnimationFrameStartEvent.class, "[spawned] [display] anim[ation] frame start[ed]")
                    .description("Called when a frame begins animating on a spawned group")
                    .examples("#3.0.0 and later",
                            "on spawned anim frame start:",
                            "\tset {_frameId} to event's frame id")
                    .since("2.6.2");
            EventValues.registerEventValue(AnimationFrameStartEvent.class, ActiveGroup.class, AnimationFrameStartEvent::getGroup);
            EventValues.registerEventValue(AnimationFrameStartEvent.class, SpawnedDisplayAnimation.class, AnimationFrameStartEvent::getAnimation);
            EventValues.registerEventValue(AnimationFrameStartEvent.class, SpawnedDisplayAnimationFrame.class, AnimationFrameStartEvent::getFrame);
            EventValues.registerEventValue(AnimationFrameStartEvent.class, DisplayAnimator.class, AnimationFrameStartEvent::getAnimator);

            //Animation Frame End Event
            Skript.registerEvent("Animation Frame End", SimpleEvents.class, AnimationFrameEndEvent.class, "[spawned] [display] anim[ation] frame (complete[d]|end[ed])")
                    .description("Called when a frame ends animating on a spawned group.",
                            "Ignores frame delay and is called after translation of parts")
                    .examples("#3.0.0 and later",
                            "on spawned anim frame end:",
                            "\tset {_frameId} to event's frame id")
                    .since("2.6.2");
            EventValues.registerEventValue(AnimationFrameEndEvent.class, ActiveGroup.class, AnimationFrameEndEvent::getGroup);
            EventValues.registerEventValue(AnimationFrameEndEvent.class, SpawnedDisplayAnimation.class, AnimationFrameEndEvent::getAnimation);
            EventValues.registerEventValue(AnimationFrameEndEvent.class, SpawnedDisplayAnimationFrame.class, AnimationFrameEndEvent::getFrame);
            EventValues.registerEventValue(AnimationFrameEndEvent.class, DisplayAnimator.class, AnimationFrameEndEvent::getAnimator);

            //Animation Complete Event
            Skript.registerEvent("Animation Complete", SimpleEvents.class, AnimationCompleteEvent.class, "[spawned] [display] anim[ation] (complete[d]|end[ed])")
                    .description("Called at the completion of a spawned animation. This is not called for looping animations/animators")
                    .since("2.6.2");
            EventValues.registerEventValue(AnimationCompleteEvent.class, ActiveGroup.class, AnimationCompleteEvent::getGroup);
            EventValues.registerEventValue(AnimationCompleteEvent.class, SpawnedDisplayAnimation.class, AnimationCompleteEvent::getAnimation);
            EventValues.registerEventValue(AnimationCompleteEvent.class, DisplayAnimator.class, AnimationCompleteEvent::getAnimator);

            //Packet Animation Start Event
            Skript.registerEvent("Packet Animation Start", SimpleEvents.class, PacketAnimationStartEvent.class, "packet [display] anim[ation] start[ed]")
                    .description("Called when a display animator starts playing a spawned animation using packets.",
                            "If players are not specified, the animation is shown to all players who can see the group")
                    .since("3.2.1");
            EventValues.registerEventValue(PacketAnimationStartEvent.class, SpawnedDisplayEntityGroup.class, g -> (SpawnedDisplayEntityGroup) g.getGroup());
            EventValues.registerEventValue(PacketAnimationStartEvent.class, SpawnedDisplayAnimation.class, PacketAnimationStartEvent::getAnimation);
            EventValues.registerEventValue(PacketAnimationStartEvent.class, DisplayAnimator.class, PacketAnimationStartEvent::getAnimator);
            EventValues.registerEventValue(PacketAnimationStartEvent.class, Player[].class, e -> e.getPlayers().toArray(new Player[0]));

            //Packet Animation Loop Start Event
            Skript.registerEvent("Packet Animation Loop Start", SimpleEvents.class, PacketAnimationLoopStartEvent.class, "packet [display] anim[ation] loop start[ed]")
                    .description("Called when a display animator begins a new animation loop using packets.",
                            "If players are not specified, the animation is shown to all players who can see the group.")
                    .since("3.0.0");
            EventValues.registerEventValue(PacketAnimationLoopStartEvent.class, ActiveGroup.class, PacketAnimationLoopStartEvent::getGroup);
            EventValues.registerEventValue(PacketAnimationLoopStartEvent.class, SpawnedDisplayAnimation.class, PacketAnimationLoopStartEvent::getAnimation);
            EventValues.registerEventValue(PacketAnimationLoopStartEvent.class, DisplayAnimator.class, PacketAnimationLoopStartEvent::getAnimator);
            EventValues.registerEventValue(PacketAnimationLoopStartEvent.class, Player[].class, e -> e.getPlayers().toArray(new Player[0]));

            //Packet Animation Frame Start Event
            Skript.registerEvent("Packet Animation Frame Start", SimpleEvents.class, PacketAnimationFrameStartEvent.class, "packet [display] anim[ation] frame start[ed]")
                    .description("Called when a frame begins animating on a spawned group using packets.",
                            "If players are not specified, the animation is shown to all players who can see the group.")
                    .examples("on packet anim frame start:",
                            "\tset {_frameId} to event's frame id")
                    .since("3.0.0");
            EventValues.registerEventValue(PacketAnimationFrameStartEvent.class, ActiveGroup.class, PacketAnimationFrameStartEvent::getGroup);
            EventValues.registerEventValue(PacketAnimationFrameStartEvent.class, SpawnedDisplayAnimation.class, PacketAnimationFrameStartEvent::getAnimation);
            EventValues.registerEventValue(PacketAnimationFrameStartEvent.class, SpawnedDisplayAnimationFrame.class, PacketAnimationFrameStartEvent::getFrame);
            EventValues.registerEventValue(PacketAnimationFrameStartEvent.class, DisplayAnimator.class, PacketAnimationFrameStartEvent::getAnimator);
            EventValues.registerEventValue(PacketAnimationFrameStartEvent.class, Player[].class, e -> e.getPlayers().toArray(new Player[0]));

            //Packet Animation Frame End Event
            Skript.registerEvent("Packet Animation Frame End", SimpleEvents.class, PacketAnimationFrameEndEvent.class, "packet [display] anim[ation] frame (complete[d]|end[ed])")
                    .description("Called when a frame ends packet animation on a spawned group.",
                            "Ignores frame delay and is called after translation of parts.",
                            "If players are not specified, the animation is shown to all players who can see the group.")
                    .examples("on packet anim frame end:",
                            "\tset {_frameId} to event's frame id")
                    .since("3.0.0");
            EventValues.registerEventValue(PacketAnimationFrameEndEvent.class, ActiveGroup.class, PacketAnimationFrameEndEvent::getGroup);
            EventValues.registerEventValue(PacketAnimationFrameEndEvent.class, SpawnedDisplayAnimation.class, PacketAnimationFrameEndEvent::getAnimation);
            EventValues.registerEventValue(PacketAnimationFrameEndEvent.class, SpawnedDisplayAnimationFrame.class, PacketAnimationFrameEndEvent::getFrame);
            EventValues.registerEventValue(PacketAnimationFrameEndEvent.class, DisplayAnimator.class, PacketAnimationFrameEndEvent::getAnimator);
            EventValues.registerEventValue(PacketAnimationFrameEndEvent.class, Player[].class, e -> e.getPlayers().toArray(new Player[0]));

            //Packet Animation Complete Event
            Skript.registerEvent("Packet Animation Complete", SimpleEvents.class, PacketAnimationCompleteEvent.class, "packet [display] anim[ation] (complete[d]|end[ed])")
                    .description("Called at the completion of a packet spawned animation. This is not called for looping animations/animators.",
                            "If players are not specified, the animation is shown to all players who can see the group.")
                    .since("3.0.0");
            EventValues.registerEventValue(PacketAnimationCompleteEvent.class, ActiveGroup.class, PacketAnimationCompleteEvent::getGroup);
            EventValues.registerEventValue(PacketAnimationCompleteEvent.class, SpawnedDisplayAnimation.class, PacketAnimationCompleteEvent::getAnimation);
            EventValues.registerEventValue(PacketAnimationCompleteEvent.class, DisplayAnimator.class, PacketAnimationCompleteEvent::getAnimator);
            EventValues.registerEventValue(PacketAnimationCompleteEvent.class, Player[].class, e -> e.getPlayers().toArray(new Player[0]));

            //Pre Interaction Click Event
            Skript.registerEvent("Pre Interaction Click", SimpleEvents.class, PreInteractionClickEvent.class, "pre interaction [entity] click[ed]")
                    .description("Called when an interaction entity is clicked before the Interaction Click Event, but does not load commands")
                    .since("2.6.2");
            EventValues.registerEventValue(PreInteractionClickEvent.class, Player.class, e -> e.getPlayer());
            EventValues.registerEventValue(PreInteractionClickEvent.class, Interaction.class, e -> e.getInteraction());
            EventValues.registerEventValue(PreInteractionClickEvent.class, InteractionClickEvent.ClickType.class, e -> e.getClickType());

            //Interaction Click Event
            Skript.registerEvent("Interaction Click", SimpleEvents.class, InteractionClickEvent.class, "interaction [entity] click[ed]")
                    .description("Called when an interaction entity is clicked. Interaction commands can only be retrieved AFTER version 2.7.7")
                    .since("2.6.2");
            EventValues.registerEventValue(InteractionClickEvent.class, Player.class, e -> e.getPlayer());
            EventValues.registerEventValue(InteractionClickEvent.class, Interaction.class, e -> e.getInteraction());
            EventValues.registerEventValue(InteractionClickEvent.class, InteractionClickEvent.ClickType.class, e -> e.getClickType());
            EventValues.registerEventValue(InteractionClickEvent.class, InteractionCommand[].class, e -> e.getCommands().toArray(new InteractionCommand[0]));

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
