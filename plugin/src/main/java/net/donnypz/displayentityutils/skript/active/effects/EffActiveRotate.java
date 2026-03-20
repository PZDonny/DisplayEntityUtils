package net.donnypz.displayentityutils.skript.active.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Rotate Active Group/Part")
@Description("Update the axis rotation of an active group/part, in degrees. \nThis only applies to display entity parts and display entities in groups")
@Examples({
        "deu rotate y of {_activegroup} by 45",
        "",
        "deu rotate z of {_activepart} by 20",
        "deu rotate world y of {_activepart} by 90"
        })
@Since("3.4.3")
public class EffActiveRotate extends Effect {
    Expression<?> object;
    Expression<Number> rotation;
    boolean worldRot;
    char axis;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveRotate.class)
                        .addPattern("deu rotate [:world] (:x|:y|:z) of %activegroup/activepart% by %number%")
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
        worldRot = parseResult.hasTag("world");
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object obj = object.getSingle(event);
        if (obj == null) return;

        Number rot = rotation.getSingle(event);
        if (rot == null) return;

        float rotRad = (float) Math.toRadians(rot.doubleValue());
        Quaternionf q = new Quaternionf();
        if (axis == 'x'){
            q.rotateX(rotRad);
        }
        else if (axis == 'y'){
            q.rotateY(rotRad);
        }
        else if (axis == 'z') {
            q.rotateZ(rotRad);
        }

        if (obj instanceof ActiveGroup<?> gr){
            gr.rotateDisplays(q);
        }
        else if (obj instanceof ActivePart part){
            part.rotateDisplay(q, worldRot);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "rotate: "+object.toString(event, debug)+"'s "+axis+" axis";
    }
}
