package net.donnypz.displayentityutils.skript.active.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartHolder;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Rotate/Pivot")
@Description(
        """
        Update the axis rotation of an active group/part or display entity, in degrees. \
        
        In groups, this only applies to display entity parts and display entities.\
        
        Using this on a non-display entity will pivot it around a location, as long as location is provided.
        """)
@Examples({
        "deu rotate y of {_activegroup} by 45 degrees",
        "deu rotate y of {_activegroup} and its non-displays by 60 degrees",
        "deu rotate world y axis of {_partfilter} by 30 degrees around {_pivotlocation}",
        "deu rotate z axis of {_display} by 90 degrees around {_pivotlocation}",
        "deu rotate world y on {_interaction} by 60 degrees around {_pivotlocation}",
        "",
        "#Before 3.6.0",
        "deu rotate y of {_activegroup} by 45",
        "",
        "deu rotate z of {_activepart} by 20",
        "deu rotate world y of {_activepart} by 90"
        })
@Since("3.4.3, 3.6.0 (Rotate around, more types)")
public class EffActiveRotate extends Effect {
    Expression<?> object;
    Expression<Number> rotation;
    Expression<Location> pivotLocation;
    boolean world;
    boolean pivotNonDisplays;
    char axis;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveRotate.class)
                        .addPattern("deu rotate [:world] (:x|:y|:z) [axis] (on|of) %activegroups/activeparts/partfilters/entities% [n:(with|including|and) [its] non( |-)displays] by %number% [degrees] [a:[pivot(ed|ing)] around %-location%]")
                        .supplier(EffActiveRotate::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        rotation = (Expression<Number>) expressions[1];
        if (parseResult.hasTag("x")){
            axis = 'x';
        } else if (parseResult.hasTag("y")) {
            axis = 'y';
        } else if (parseResult.hasTag("z")) {
            axis = 'z';
        }
        world = parseResult.hasTag("world");
        pivotNonDisplays = parseResult.hasTag("n");
        pivotLocation = (Expression<Location>) expressions[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object[] objects = object.getArray(event);
        if (objects == null) return;

        Number rot = rotation.getSingle(event);
        if (rot == null) return;

        Location pivotLoc = pivotLocation == null ? null : pivotLocation.getSingle(event);
        float rotRad = (float) Math.toRadians(rot.doubleValue());
        Quaternionf rotation = new Quaternionf();

        if (axis == 'x'){
            rotation.rotateX(rotRad);
        }
        else if (axis == 'y'){
            rotation.rotateY(rotRad);
        }
        else if (axis == 'z') {
            rotation.rotateZ(rotRad);
        }

        for (Object o : objects){
            if (o instanceof ActivePartHolder<?> h){
                if (pivotNonDisplays){
                    h.pivotOrRotateAround(rotation,
                            pivotLoc == null ? h.getLocation() : pivotLoc,
                            world);
                    return;
                }
                //fall through if not pivoting non-displays
            }
            if (o instanceof Active a){
                if (pivotLoc == null){
                    boolean isNonDisplay = (a instanceof ActivePart p && !p.isDisplay());
                    if (isNonDisplay){
                        sendPivotRequiredError();
                        return;
                    }
                    a.rotate(rotation, world);
                }
                else{
                    a.pivotOrRotateAround(rotation, pivotLoc, world);
                }
            }
            else if (o instanceof Display display){
                if (pivotLoc == null) DisplayUtils.rotate(display, rotation, world);
                else DisplayUtils.rotateAround(display, rotation, pivotLoc, world);
            }
            else if (o instanceof Entity e){
                if (pivotLoc != null){
                    DisplayUtils.pivot(e, rotation, pivotLoc, world);
                    return;
                }
                sendPivotRequiredError();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return String.format("rotate %s on %s %s axis",
                object.toString(event, debug),
                axis,
                world ? "world" : "local");
    }

    private void sendPivotRequiredError(){
        Skript.error("A pivot location is requiring when trying to pivot a non-display around a location ", ErrorQuality.SEMANTIC_ERROR);
    }
}
