package net.donnypz.displayentityutils.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.InteractionClickEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.FollowType;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.skriptlang.skript.lang.converter.Converters;

public class SkriptTypes {

    static{

        Classes.registerClass(new ClassInfo<>(ActiveGroup.class, "activegroup")
                .user("active( |-)?(group|model)s?")
                .name("Active Group")
                .description("An ambiguous representation of a Spawned Group or Packet Group")
                .examples()
                .defaultExpression(new EventValueExpression<>(ActiveGroup.class))
                .since("3.0.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(ActiveGroup o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(ActiveGroup o) {
                        return "activegroup w/ tag: " + o.getTag();
                    }
                })
        );
        Converters.registerConverter(ActiveGroup.class, Location.class, ActiveGroup::getLocation);

        Classes.registerClass(new ClassInfo<>(SpawnedDisplayEntityGroup.class, "spawnedgroup")
                .user("spawned( |-)?(group|model)s?")
                .name("Spawned Group")
                .description("Represents a Display Entity Group/Model spawned in the game world")
                .examples()
                .defaultExpression(new EventValueExpression<>(SpawnedDisplayEntityGroup.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(SpawnedDisplayEntityGroup o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(SpawnedDisplayEntityGroup o) {
                        return "spawnedgroup w/ tag: " + o.getTag();
                    }
                })
        );
        Converters.registerConverter(SpawnedDisplayEntityGroup.class, Location.class, SpawnedDisplayEntityGroup::getLocation);

        Classes.registerClass(new ClassInfo<>(PacketDisplayEntityGroup.class, "packetgroup")
                .user("packet( |-)?(group|model)s?")
                .name("Packet Group")
                .description("Represents a packet-based Display Entity Group/Model")
                .examples()
                .defaultExpression(new EventValueExpression<>(PacketDisplayEntityGroup.class))
                .since("3.0.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(PacketDisplayEntityGroup o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(PacketDisplayEntityGroup o) {
                        return "packetgroup w/ tag: " + o.getTag();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(DisplayEntityGroup.class, "savedgroup")
                .user("saved( |-)?(group|model)s?")
                .name("Saved Group")
                .description("Represents a saved Display Entity Group/Model")
                .examples()
                .defaultExpression(new EventValueExpression<>(DisplayEntityGroup.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(DisplayEntityGroup o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(DisplayEntityGroup o) {
                        return "savedgroup w/ tag: " + o.getTag();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(GroupSpawnSettings.class, "groupspawnsetting")
                .user("(group( |-)?)?spawn( |-)?settings?")
                .name("Group Spawn Settings")
                .description("Represents spawn settings that can be applied when spawning a saved group")
                .examples()
                .defaultExpression(new EventValueExpression<>(GroupSpawnSettings.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(GroupSpawnSettings o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(GroupSpawnSettings o) {
                        return "group spawn settings";
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(ActivePart.class, "activepart")
                .user("active( |-)?parts?")
                .name("Active Part")
                .description("Represents an individual Display/Interaction entity from a spawned Display Entity Group/Model.",
                        "This can be either a Spawned Part or Packet Part.")
                .examples()
                .defaultExpression(new EventValueExpression<>(ActivePart.class))
                .since("3.0.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(ActivePart o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(ActivePart o) {
                        String groupTag = o.getGroup() != null ? o.getGroup().getTag() : "GROUPLESS";
                        return "activepart w/ partUUID: " + o.getPartUUID()+" | (GROUP TAG:"+groupTag+")";
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(SpawnedDisplayEntityPart.class, "spawnedpart")
                .user("spawned( |-)?parts?")
                .name("Spawned Part")
                .description("Represents an individual Display/Interaction entity from a spawned Display Entity Group/Model.",
                        "A spawned part can be used as an entity in other expressions, effects, etc.")
                .examples()
                .defaultExpression(new EventValueExpression<>(SpawnedDisplayEntityPart.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(SpawnedDisplayEntityPart o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(SpawnedDisplayEntityPart o) {
                        String groupTag = o.getGroup() != null ? o.getGroup().getTag() : "GROUPLESS";
                        return "spawnedpart w/ partUUID: " + o.getPartUUID()+" | (GROUP TAG:"+groupTag+")";
                    }
                })
        );
        Converters.registerConverter(SpawnedDisplayEntityPart.class, Entity.class, SpawnedDisplayEntityPart::getEntity);

        Classes.registerClass(new ClassInfo<>(PacketDisplayEntityPart.class, "packetpart")
                .user("packet( |-)?parts?")
                .name("Packet Part")
                .description("Represents an individual Display/Interaction entity from a packet group.")
                .examples()
                .defaultExpression(new EventValueExpression<>(PacketDisplayEntityPart.class))
                .since("3.0.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(PacketDisplayEntityPart o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(PacketDisplayEntityPart o) {
                        String groupTag = o.getGroup() != null ? o.getGroup().getTag() : "GROUPLESS";
                        return "packetedpart w/ partUUID: " + o.getPartUUID()+" | (GROUP TAG:"+groupTag+")";
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(MultiPartSelection.class, "multipartselection")
                .user("(multi( |-)?)?part( |-)?selection")
                .name("Multi Part Selection")
                .description("Represents a selection of spawned parts from a spawned Display Entity Group/Model.",
                            "This can be either a Spawned Part Selection or a Packet Part Selection")
                .examples()
                .defaultExpression(new EventValueExpression<>(MultiPartSelection.class))
                .since("3.1.1")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(MultiPartSelection o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(MultiPartSelection o) {
                        return "multipartselection w/ size: " + o.getSize();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(SpawnedPartSelection.class, "partselection")
                .user("(spawned( |-)?)?part( |-)?selection")
                .name("Spawned Part Selection")
                .description("Represents a selection of spawned parts from a spawned Display Entity Group/Model.")
                .examples()
                .defaultExpression(new EventValueExpression<>(SpawnedPartSelection.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(SpawnedPartSelection o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(SpawnedPartSelection o) {
                        return "partselection w/ size: " + o.getSize();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(PacketPartSelection.class, "packetpartselection")
                .user("packet( |-)?part( |-)?selection")
                .name("Packet Part Selection")
                .description("Represents a selection of packet-based parts from a packet-based Display Entity Group/Model.")
                .examples()
                .defaultExpression(new EventValueExpression<>(PacketPartSelection.class))
                .since("3.0.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(PacketPartSelection o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(PacketPartSelection o) {
                        return "packetpartselection w/ size: " + o.getSize();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(DisplayAnimator.class, "displayanimator")
                .user("(display)animator")
                .name("DisplayAnimator")
                .description("Used to play an animation on a spawnedgroup")
                .examples()
                .defaultExpression(new EventValueExpression<>(DisplayAnimator.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(DisplayAnimator o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(DisplayAnimator o) {
                        return "display animator w/ type: " + o.getAnimationType();
                    }
                })
        );


        Classes.registerClass(new ClassInfo<>(SpawnedDisplayAnimation.class, "spawnedanimation")
                .user("spawned( |-)?anim(ation)?")
                .name("Spawned Animation")
                .description("Represents an animation that can be played on a spawned Display Entity Group/Model")
                .examples()
                .defaultExpression(new EventValueExpression<>(SpawnedDisplayAnimation.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(SpawnedDisplayAnimation o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(SpawnedDisplayAnimation o) {
                        return "spawnedanimation w/ tag: " + o.getAnimationTag();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(DisplayAnimation.class, "savedanimation")
                .user("saved( |-)?anim(ation)?")
                .name("Saved Animation")
                .description("Represents a saved animation")
                .examples()
                .defaultExpression(new EventValueExpression<>(DisplayAnimation.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(DisplayAnimation o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(DisplayAnimation o) {
                        return "savedanimation w/ tag: " + o.getAnimationTag();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(SpawnedDisplayAnimationFrame.class, "spawnedanimationframe")
                .user("spawned( |-)?(anim(ation)?)?( |-)?frame")
                .name("Spawned Animation Frame")
                .description("Represents an Animation Frame from a spawned Display Animation")
                .examples()
                .defaultExpression(new EventValueExpression<>(SpawnedDisplayAnimationFrame.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(SpawnedDisplayAnimationFrame o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(SpawnedDisplayAnimationFrame o) {
                        return "spawnedanimationframe w/ tag: " + o.getTag();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(FramePoint.class, "framepoint")
                .user("frame( |-)?point")
                .name("Frame Point")
                .description("Represents a Frame Point from a Spawned Display Animation Frame")
                .examples()
                .defaultExpression(new EventValueExpression<>(FramePoint.class))
                .since("3.2.1")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(FramePoint o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(FramePoint o) {
                        return "framepoint w/ tag: " + o.getTag();
                    }
                })
        );


        Classes.registerClass(new ClassInfo<>(InteractionCommand.class, "interactioncommand")
                .user("interaction( |-)?c(ommand|md)")
                .name("Interaction Command")
                .description("Represents an Interaction Command stored on an Interaction Entity")
                .examples()
                .defaultExpression(new EventValueExpression<>(InteractionCommand.class))
                .since("2.6.2")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(InteractionCommand o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(InteractionCommand o) {
                        return "interaction command: " + o.getCommand();
                    }
                })
        );



        EnumWrapper<SpawnedDisplayEntityPart.PartType> partTypeWrapper = new EnumWrapper<>(SpawnedDisplayEntityPart.PartType.class, "parttype", null);
        Classes.registerClass(partTypeWrapper.getClassInfo("parttype")
                .user("(part( | -)?)?type")
                .name("Part Type")
                .description("Represents a part type being BLOCK_DISPLAY, ITEM_DISPLAY, TEXT_DISPLAY, or INTERACTION")
                .since("2.6.2"));

        EnumWrapper<InteractionClickEvent.ClickType> clickTypeWrapper = new EnumWrapper<>(InteractionClickEvent.ClickType.class, "iClickType", null);
        Classes.registerClass(clickTypeWrapper.getClassInfo("interactionclicktype")
                .user("(click( | -)?)?type")
                .name("Interaction Click Type")
                .description("Represents a interaction click type of LEFT or RIGHT")
                .since("2.6.2"));

        EnumWrapper<DisplayAnimator.AnimationType> animationTypeWrapper = new EnumWrapper<>(DisplayAnimator.AnimationType.class, "animtype", null);
        Classes.registerClass(animationTypeWrapper.getClassInfo("animationtype")
                .user("(anim(ation)?( | -)?)?type")
                .name("Animation Type")
                .description("Represents an animation type, being LINEAR or LOOP")
                .since("2.6.2"));

        EnumWrapper<GroupSpawnedEvent.SpawnReason> spawnReasonWrapper = new EnumWrapper<>(GroupSpawnedEvent.SpawnReason.class, "gsr", null);
        Classes.registerClass(spawnReasonWrapper.getClassInfo("groupspawnreason")
                .user("(group( |-)?spawn( |-)?)?reason")
                .name("Group Spawn Reason")
                .description("Represents a spawn reason when a saved Group/Model is spawned.",
                            "\"INTERNAL\" spawn reason added in version 3.0.0")
                .since("2.6.2"));

        EnumWrapper<FollowType> followTypeWrapper = new EnumWrapper<>(FollowType.class, "ft", null);
        Classes.registerClass(followTypeWrapper.getClassInfo("followtype")
                .user("follow( |-)?type")
                .name("Follow Type")
                .description("Represents a follow type that can be used when a group follows an entity's looking direction.")
                .since("3.2.1"));

    }
}
