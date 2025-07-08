package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Glow Color")
@Description("Set the glow color of a spawned group/part or part selection. Or get the glow color of a spawned group")
@Examples({"set {_spawnedpart}'s deu glow color to red", "set {_color} to {_spawnedgroup}'s deu glow color"})
@Since("2.6.2")
public class ExprSpawnedGlowColor extends SimplePropertyExpression<Object, Color> {
    static {
        register(ExprSpawnedGlowColor.class, Color.class, "[the] deu glow[ing] colo[u]r [override]", "spawnedgroups/spawnedparts/partselections");
    }

    @Override
    public Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    @Nullable
    public Color convert(Object object) {
        if (object instanceof SpawnedDisplayEntityGroup group){
            if (group.getGlowColor() != null){
                return ColorRGB.fromBukkitColor(group.getGlowColor());
            }
            return null;
        }
        else if (object instanceof SpawnedDisplayEntityPart part){
            if (part.getGlowColor() != null){
                return ColorRGB.fromBukkitColor(part.getGlowColor());
            }
            else if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Skript.error("You can not get the glow color of an INTERACTION spawned part");
            }
            return null;
        }

        Skript.error("You can not get the glow color of a part selection", ErrorQuality.SEMANTIC_ERROR);
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "deu glow color";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Object o = getExpr().getSingle(event);
        if (o == null){
            return;
        }
        Active spawned = (Active) o;

        switch (mode) {
            case SET -> {
                if (delta == null){
                    return;
                }
                Color color = (Color) delta[0];
                spawned.setGlowColor(color.asBukkitColor());
            }
            case RESET -> {
                spawned.setGlowColor(null);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Color.class);
        }
        return null;
    }
}
