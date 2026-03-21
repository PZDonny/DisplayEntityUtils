package net.donnypz.displayentityutils.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.InteractionClickEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.skriptlang.skript.lang.converter.Converters;

public class SkriptTypes {

    public static class FrameId{
        int id;

        public FrameId(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

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

        Classes.registerClass(new ClassInfo<>(ActivePart.class, "activepart")
                .user("active( |-)?parts?")
                .name("Active Part")
                .description("Represents an entity from an active Display Entity Group/Model.",
                        "\nThis can be packet-based.")
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
                .description("Represents an individual entity from an active Display Entity Group/Model.",
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
                .description("Represents an individual entity from a packet-based active group.")
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
                        return "packetpart w/ partUUID: " + o.getPartUUID()+" | (GROUP TAG:"+groupTag+")";
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(MultiPartSelection.class, "partfilter")
                .user("part( |-)?filters?")
                .name("Part Filter")
                .description("Represents a selection of filtered parts from an Active Group. This may be packet based.")
                .examples()
                .defaultExpression(new EventValueExpression<>(MultiPartSelection.class))
                .since("3.1.1 (multipartselection), 3.3.4 (multipartfilter), 3.5.0 (partfilter)")
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
                        return "partfilter w/ size: " + o.getSize();
                    }
                })
        );


        Classes.registerClass(new ClassInfo<>(DisplayAnimator.class, "displayanimator")
                .user("(display)?animator")
                .name("DisplayAnimator")
                .description("Used to play an animation on activegroups")
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


        Classes.registerClass(new ClassInfo<>(SpawnedDisplayAnimation.class, "deuanimation")
                .user("deu( |-)?anim(ation)?")
                .name("Animation")
                .description("Represents an animation that can be played on an activegroup")
                .examples()
                .defaultExpression(new EventValueExpression<>(SpawnedDisplayAnimation.class))
                .since("3.3.1")
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
                        return "animation w/ tag: " + o.getAnimationTag();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(SpawnedDisplayAnimationFrame.class, "deuanimationframe")
                .user("deu( |-)?(anim(ation)?( |-)?)?frame")
                .name("Animation Frame")
                .description("Represents an Animation Frame from an Animation")
                .examples()
                .defaultExpression(new EventValueExpression<>(SpawnedDisplayAnimationFrame.class))
                .since("3.3.1")
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
                        return "animationframe w/ tag: " + o.getTag();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(FramePoint.class, "framepoint")
                .user("frame( |-)?point")
                .name("Frame Point")
                .description("Represents a Frame Point from an Animation Frame")
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

        Classes.registerClass(new ClassInfo<>(FrameId.class, "frameid")
                .user("frame( |-)?id")
                .name("Frame Id")
                .description("Represents the frame id of ab Animation Frame")
                .examples()
                .defaultExpression(new EventValueExpression<>(FrameId.class))
                .since("3.5.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(FrameId o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(FrameId o) {
                        return String.valueOf(o.id);
                    }
                })
        );
        Converters.registerConverter(FrameId.class, Number.class, FrameId::getId);


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
                        return (o.isConsoleCommand() ? "console" : "player")+" interaction command: " + o.getCommand();
                    }
                })
        );
        Converters.registerConverter(InteractionCommand.class, String.class, InteractionCommand::getCommand);

        EnumWrapper<SpawnedDisplayEntityPart.PartType> partTypeWrapper = new EnumWrapper<>(SpawnedDisplayEntityPart.PartType.class, null, null);
        partTypeWrapper.replace("interaction", "deu_interaction");
        partTypeWrapper.replace("mannequin", "deu_mannequin");
        Classes.registerClass(partTypeWrapper.getClassInfo("parttype")
                .user("part( | -)?type")
                .name("Part Type")
                .description("Represents a part's (display, interaction, etc.) type")
                .examples(
                        "if {_activepart}'s active part type is deu_interaction:",
                        "\tbroadcast \"this part is an interaction!\"",
                        "",
                        "if {_activepart}'s active part type is text_display:",
                        "\tbroadcast \"this part is a text display!\""
                )
                .since("2.6.2, 3.3.6 (Removed \"parttype_\" prefix), 3.4.0 (Mannequin)"));

        EnumWrapper<InteractionClickEvent.ClickType> clickTypeWrapper = new EnumWrapper<>(InteractionClickEvent.ClickType.class, "iclicktype", null);
        Classes.registerClass(clickTypeWrapper.getClassInfo("interactionclicktype")
                .user("i(nteraction)?( | -)?click( | -)?type")
                .name("Interaction Click Type")
                .description("Represents a interaction click type of LEFT or RIGHT")
                .since("2.6.2"));

        EnumWrapper<GroupSpawnedEvent.SpawnReason> spawnReasonWrapper = new EnumWrapper<>(GroupSpawnedEvent.SpawnReason.class, "gsr", null);
        Classes.registerClass(spawnReasonWrapper.getClassInfo("groupspawnreason")
                .user("group( |-)?spawn( |-)?reason")
                .name("Group Spawn Reason")
                .description("Represents a spawn reason when a saved Group/Model is spawned.")
                .since("2.6.2, 3.0.0 (INTERNAL)"));
    }
}