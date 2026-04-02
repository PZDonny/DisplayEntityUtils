package net.donnypz.displayentityutils.skript.io.sections;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.command.bdengine.BDEngineSpawnModelCMD;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.io.File;
import java.util.List;


@Name("Spawn BDEngine Model from File")
@Description({"Spawn a BDEngine Model, from a file, at a location, with specified options",
        "**Entries**",
        "`packet` = whether the group should be packet-based. False by default",
        "`teleport-duration` = the teleport-duration of display entities in the group. 0 by default",
        "`billboard` = the billboard of display entities in the group. FIXED by default",
        "`persistent` = the persistence of **all** entities in the group. True by default",
        "`visible` = whether the group should be visible. True by default",
        "`brightness` = the brightness of display entities in the group. Use `-1 and -1` for default brightness",
        "`spawnanimation` = whether the group should play its spawn animation. True by default",
        "",
        "If a group is packet-based it's persistence cannot be set within the section and must be set afterwards."
})
@Examples({
        "deu spawn bde model \"mymodel\" at {_location}",
        "deu spawn bde model \"mymodel\" at {_location} stored in {_activegroup}",
        "",
        "deu spawn bde model \"mymodel\" at {_location} stored in {_activegroup}:",
        "\tpacket: false",
        "\tteleport-duration: 2",
        "\tbillboard: VERTICAL",
        "\tpersistent: true",
        "\tvisible: true",
        "\tbrightness: 10 and 5 #Block and Sky, -1 and -1 to reset",
        "\tspawnanimation: true",
        "",
        "",
        "#3.4.3 and earlier",
        "set {_activegroup} to bdengine model \"mymodel\" spawned at {_location}",
        "set {_activegroup} to bde model \"model.bdengine\" spawned at {_location}"
})
@Since("3.3.0, 3.5.0 (New Syntax), 3.5.1 (Section)")
@DocumentationId("EffBDEModelToSpawned")
public class SecSpawnBDEModel extends EffectSection {

    private Expression<String> fileName;
    Expression<Location> location;
    private Expression<?> store;

    private Expression<Boolean> packetExpr;
    private Expression<Integer> teleportDurationExpr;
    private Expression<Display.Billboard> billboardExpr;
    private Expression<Boolean> persistentExpr;
    private Expression<Boolean> visibleExpr;
    private Expression<Number> brightnessExpr;
    private Expression<Boolean> spawnAnimationExpr;

    private static EntryValidator VALIDATOR;

    public static void register(SyntaxRegistry registry){
        VALIDATOR = EntryValidator.builder()
                .addEntryData(new ExpressionEntryData<>("packet", null, true, Boolean.class))
                .addEntryData(new ExpressionEntryData<>("teleport-duration", null, true, Integer.class))
                .addEntryData(new ExpressionEntryData<>("billboard", null, true, Display.Billboard.class))
                .addEntryData(new ExpressionEntryData<>("persistent", null, true, Boolean.class))
                .addEntryData(new ExpressionEntryData<>("visible", null, true, Boolean.class))
                .addEntryData(new ExpressionEntryData<>("brightness", null, true, Number.class))
                .addEntryData(new ExpressionEntryData<>("spawnanimation", null, true, Boolean.class))
                .build();

        registry.register(SyntaxRegistry.SECTION,
                SyntaxInfo.builder(SecSpawnBDEModel.class)
                        .addPattern("deu spawn bde[ngine] model %string% at %location% [store:[and] store[d] [it |the result] in %-objects%]")
                        .supplier(SecSpawnBDEModel::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.fileName = (Expression<String>) expressions[0];
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
            this.spawnAnimationExpr = (Expression<Boolean>) container.getOptional("spawnanimation",false);
        }

        return true;
    }

    @Nullable
    @Override
    protected TriggerItem walk(Event event) {
        String fileName = this.fileName.getSingle(event);
        if (fileName == null){
            error("File/Model name is missing");
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

        if (persistentExpr != null){
            Boolean persist = persistentExpr.getSingle(event);
            if (persist != null){
                settings.persistentByDefault(persist);
            }
            else{
                settings.persistentByDefault(!isPacket);
            }
        }


        if (spawnAnimationExpr != null){
            Boolean play = spawnAnimationExpr.getSingle(event);
            boolean playSpawnAnim = play != null && play;
            settings.playSpawnAnimation(playSpawnAnim);
        }

        BDEModel model = BDEngineUtils.readFile(new File(PluginFolders.bdeFilesFolder, BDEngineSpawnModelCMD.fileExtension(fileName)));
        if (model == null){
            return super.walk(event, false);
        }


        ActiveGroup<?> activeGroup;
        if (isPacket){
            activeGroup = model.createPacketGroup(loc, settings);
        }
        else{
            activeGroup = model.spawn(loc, GroupSpawnedEvent.SpawnReason.SKRIPT, settings);
        }

        if (this.store != null){
            store.change(event, new ActiveGroup[]{activeGroup}, Changer.ChangeMode.SET);
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawn bdengine model from file \""+fileName.toString(event,debug);
    }
}
