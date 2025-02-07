package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Set Billboard")
@Description("Set the billboard of a spawned group/part/selection")
@Examples({"set {_setting}'s billboard to vertical", "set {_setting}'s billboard to center for parts with tag \"pivotpart\""})
@Since("2.6.2")
public class EffSpawnedBillboard extends Effect {
    static {
        Skript.registerEffect(EffSpawnedBillboard.class,"[deu] (set|change) %spawnedgroup/spawnedpart/partselection%['s] [display] billboard to %billboard%");
    }

    Expression<?> object;
    Expression<Display.Billboard> billboard;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        billboard = (Expression<Display.Billboard>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Spawned spawned = (Spawned) object.getSingle(event);
        Display.Billboard b = billboard.getSingle(event);
        if (spawned != null && b != null) {
            spawned.setBillboard(b);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "billboard: "+object.toString(event, debug);
    }
}
