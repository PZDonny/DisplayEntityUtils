package net.donnypz.displayentityutils.skript.io.effects;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.command.bdengine.BDEngineSpawnModelCMD;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.io.File;

@Name("BDEngine Model to Spawned Group")
@Description("Spawn a model from a \".bdengine\" file, stored in DisplayEntityUtils' \"bdenginefiles\" folder")
@Examples({
        "deu spawn bde model \"mymodel\" at {_location}",
        "deu spawn bde model \"mymodel\" at {_location} stored in {_newgroup}",
        "",
        "3.4.3 and earlier",
        "set {_spawnedgroup} to bdengine model \"mymodel\" spawned at {_location}",
        "set {_spawnedgroup} to bde model \"model.bdengine\" spawned at {_location}"
})
@Since("3.3.0")
public class EffBDEModelToSpawned extends Effect {

    private Expression<String> fileName;
    Expression<Location> location;
    private Expression<?> store;


    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffBDEModelToSpawned.class)
                        .addPattern("deu spawn bde[ngine] model %string% at %location% [store:[and] store[d] [it |the result] in %-objects%]")
                        .supplier(EffBDEModelToSpawned::new)
                        .build()
        );
    }


    @Override
    protected void execute(Event event) {
        String file = fileName.getSingle(event);
        Location loc = location.getSingle(event);
        if (file == null || loc == null){
            return;
        }
        BDEModel model = BDEngineUtils.readFile(new File(PluginFolders.bdeFilesFolder, BDEngineSpawnModelCMD.fileExtension(file)));
        if (model == null){
            return;
        }
        SpawnedDisplayEntityGroup g = model.spawn(loc, GroupSpawnedEvent.SpawnReason.SKRIPT);

        if (this.store != null){
            store.change(event, new ActiveGroup[]{g}, Changer.ChangeMode.SET);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "bdengine file \""+fileName.toString(event,debug)+"\" to spawned group";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        fileName = (Expression<String>) expressions[0];
        location = (Expression<Location>) expressions[1];
        if (parseResult.hasTag("store")){
            this.store = expressions[2];
        }
        return true;
    }


}
