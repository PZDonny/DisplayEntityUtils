package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.command.bdengine.BDEngineSpawnModelCMD;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEModel;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Name("BDEngine Model to Spawned Group")
@Description("Spawn a model from a \".bdengine\" file, stored in DisplayEntityUtils' \"bdenginefiles\" folder")
@Examples({"set {_spawnedgroup} to bdengine model \"mymodel\" spawned at {_location}",
            "set {_spawnedgroup} to bde model \"model.bdengine\" spawned at {_location}"})
@Since("3.3.0")
public class ExprBDEModelToSpawned extends SimpleExpression<SpawnedDisplayEntityGroup> {

    static{
        Skript.registerExpression(ExprBDEModelToSpawned.class, SpawnedDisplayEntityGroup.class, ExpressionType.COMBINED, "bde[ngine] model %string% spawned at %location%");
    }

    private Expression<String> fileName;
    private Expression<Location> location;

    @Override
    protected SpawnedDisplayEntityGroup @Nullable [] get(Event event) {
        String file = fileName.getSingle(event);
        Location loc = location.getSingle(event);
        if (file == null || loc == null){
            return null;
        }
        BDEModel model = BDEngineUtils.readFile(new File(PluginFolders.bdeFilesFolder, BDEngineSpawnModelCMD.fileExtension(file)));
        if (model == null) return null;
        return new SpawnedDisplayEntityGroup[]{model.spawn(loc, GroupSpawnedEvent.SpawnReason.SKRIPT)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends SpawnedDisplayEntityGroup> getReturnType() {
        return SpawnedDisplayEntityGroup.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "bdengine file \""+fileName.toString(event,debug)+"\" to spawned group";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        fileName = (Expression<String>) expressions[0];
        location = (Expression<Location>) expressions[1];
        return true;
    }
}
