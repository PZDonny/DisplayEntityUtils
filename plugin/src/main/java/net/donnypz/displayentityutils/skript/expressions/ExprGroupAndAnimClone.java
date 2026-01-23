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
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Cloned Active Group or Animation")
@Description("Create a cloned version of a active group or animation")
@Examples({"set {_groupclone} to a clone of {_spawnedgroup}",
        "",
        "#Create a group clone at a certain location",
        "set {_groupclone} to a clone of {_spawnedgroup} at {_location}",
        "",
        "#Create a group clone with group spawn settings (2.7.7+)",
        "set {_groupclone} to a clone of {_spawnedgroup} with {_settings}",
        "",
        "#Create a packet group clone (3.3.1+)",
        "set {_groupclone} to a clone of {_packetgroup}",
        "",
        "#Create an animation clone",
        "set {_animclone} to a clone of {_animation}"})
@Since("2.6.2")
public class ExprGroupAndAnimClone extends SimpleExpression<Object> {

    static{
        Skript.registerExpression(ExprGroupAndAnimClone.class, Object.class, ExpressionType.SIMPLE, "[a] (clone[d version]|cop[y|ied version]) of %activegroup/animation% [loc:at %-location%] [s:with %-groupspawnsettings%]");
    }

    private Expression<?> object;
    private Expression<Location> location;
    private Expression<GroupSpawnSettings> settings;

    @Override
    protected Object [] get(Event event) {
        Object obj = object.getSingle(event);
        if (obj instanceof SpawnedDisplayEntityGroup g){
            Location l = location == null ? g.getLocation() : location.getSingle(event);
            GroupSpawnSettings s = settings == null ? new GroupSpawnSettings() : settings.getSingle(event);

            if (l == null || s == null){
                return null;
            }
            return new SpawnedDisplayEntityGroup[]{g.clone(l, s)};
        }
        else if (obj instanceof PacketDisplayEntityGroup g){
            Location l = location == null ? g.getLocation() : location.getSingle(event);
            return new PacketDisplayEntityGroup[]{g.clone(l, true, g.isAutoShow())};
        }
        else if (obj instanceof SpawnedDisplayAnimation a){
            return new SpawnedDisplayAnimation[]{a.clone()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class; // todo: this is not recommended
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "clone "+object.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        if (parseResult.hasTag("loc")){
            location = (Expression<Location>) expressions[1];
        }
        if (parseResult.hasTag("s")){
            settings = (Expression<GroupSpawnSettings>) expressions[2];
        }
        return true;
    }
}
