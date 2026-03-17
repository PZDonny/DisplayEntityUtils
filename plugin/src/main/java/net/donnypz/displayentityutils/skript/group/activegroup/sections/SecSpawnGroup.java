package net.donnypz.displayentityutils.skript.group.activegroup.sections;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;


@Name("Spawn Group")
@Description({"Spawn a DEU Group/Model at a location, with specified options",
        "**Entries**",
        "`packet` = whether the group should be packet-based. False by default",
        "`teleport-duration` = the teleport-duration of display entities in the group. 0 by default",
        "`billboard` = the billboard of display entities in the group. FIXED by default",
        "`persistent` = the persistence of **all** entities in the group. True by default",
        "`visible` = whether the group should be visible. True by default",
        "`brightness` = the brightness of display entities in the group. Use `-1 and -1` for default brightness"
})
@Examples({"deu spawn {_savedgroup} at {_location}",
        "deu spawn {_savedgroup} at {_location} and store the result in {_activegroup}",
        "",
        "deu spawn {_savedgroup} at {_location} and store the result in {_activegroup}:",
        "\tpacket: false",
        "\tteleport-duration: 2",
        "\tbillboard: VERTICAL",
        "\tpersistent: true",
        "\tvisible: true",
        "\tbrightness: 10 and 5 #Block and Sky, -1 and -1 to reset"
})
@Since("3.5.0")
public class SecSpawnGroup extends EffectSection {

    private Expression<DisplayEntityGroup> savedGroup;
    private Expression<Location> location;
    private Expression<?> store;

    private Expression<Boolean> packetExpr;
    private Expression<Integer> teleportDurationExpr;
    private Expression<Display.Billboard> billboardExpr;
    private Expression<Boolean> persistentExpr;
    private Expression<Boolean> visibleExpr;
    private Expression<Number> brightnessExpr;

    private static EntryValidator VALIDATOR;

    public static void register(SyntaxRegistry registry){
        VALIDATOR = EntryValidator.builder()
                .addEntryData(new ExpressionEntryData<>("packet", null, true, Boolean.class))
                .addEntryData(new ExpressionEntryData<>("teleport-duration", null, true, Integer.class))
                .addEntryData(new ExpressionEntryData<>("billboard", null, true, Display.Billboard.class))
                .addEntryData(new ExpressionEntryData<>("persistent", null, true, Boolean.class))
                .addEntryData(new ExpressionEntryData<>("visible", null, true, Boolean.class))
                .addEntryData(new ExpressionEntryData<>("brightness", null, true, Number.class))
                .build();

        registry.register(SyntaxRegistry.SECTION,
                SyntaxInfo.builder(SecSpawnGroup.class)
                        .addPattern("deu spawn %savedgroup% at %location% [store:and store [it |the result] in %-objects%]")
                        .supplier(SecSpawnGroup::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.savedGroup = (Expression<DisplayEntityGroup>) expressions[0];
        this.location = (Expression<Location>) expressions[1];
        if (parseResult.hasTag("store")){
            this.store = expressions[2];
        }

        if (sectionNode == null) return true;

        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container != null) {
            this.packetExpr = (Expression<Boolean>) container.getOptional("packet",false);
            this.teleportDurationExpr = (Expression<Integer>) container.getOptional("teleport-duration",false);
            this.billboardExpr = (Expression<Display.Billboard>) container.getOptional("billboard",false);
            this.persistentExpr = (Expression<Boolean>) container.getOptional("persistent",false);
            this.visibleExpr = (Expression<Boolean>) container.getOptional("visible",false);
            this.brightnessExpr = (Expression<Number>) container.getOptional("brightness",false);
        }

        return true;
    }

    @Nullable
    @Override
    protected TriggerItem walk(Event event) {
        DisplayEntityGroup g = savedGroup.getSingle(event);
        if (g == null){
            error("Saved Group is missing");
            return super.walk(event, false);
        }

        Location loc = location.getSingle(event);
        if (loc == null){
            error("Location is missing");
            return super.walk(event, false);
        }
        GroupSpawnSettings settings = new GroupSpawnSettings();

        if (teleportDurationExpr != null){
            Integer tpDur = teleportDurationExpr.getSingle(event);
            if (tpDur != null) settings.setTeleportationDuration(tpDur);
        }

        if (billboardExpr != null){
            Display.Billboard billboard = billboardExpr.getSingle(event);
            if (billboard != null) settings.addBillboard(billboard, null);
        }


        if (persistentExpr != null){
            Boolean persist = persistentExpr.getSingle(event);
            if (persist != null) settings.persistentByDefault(persist);
        }

        if (visibleExpr != null){
            Boolean visible = visibleExpr.getSingle(event);
            if (visible != null) settings.visibleByDefault(visible, null);
        }

        if (brightnessExpr != null){
            Number[] brightnessArr = brightnessExpr.getArray(event);
            if (brightnessArr != null){
                int block = brightnessArr[1].intValue();
                int sky = brightnessArr[0].intValue();
                if (block == -1 && sky == -1){
                    settings.addBrightness(null, null);
                }
                else{
                    settings.addBrightness(new Display.Brightness(block, sky), null);
                }
            }
        }

        boolean isPacket;
        if (packetExpr != null){
            Boolean packet = packetExpr.getSingle(event);
            isPacket = packet != null && packet;
        }
        else isPacket = false;


        ActiveGroup<?> activeGroup;
        if (isPacket){
            activeGroup = g.createPacketGroup(loc, GroupSpawnedEvent.SpawnReason.SKRIPT, true, settings);
        }
        else{
            activeGroup = g.spawn(loc, GroupSpawnedEvent.SpawnReason.SKRIPT, settings);
        }

        if (this.store != null){
            store.change(event, new ActiveGroup[]{activeGroup}, Changer.ChangeMode.SET);
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "";
    }
}
