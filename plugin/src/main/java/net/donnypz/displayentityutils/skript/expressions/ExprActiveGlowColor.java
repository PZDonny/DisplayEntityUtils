package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Glow Color")
@Description("Set the glow color of a active or packet-based group, part, or part filter. Or get the glow color of a active group")
@Examples({"set {_spawnedpart}'s deu glow color to red",
        "set {_color} to {_spawnedgroup}'s deu glow color",
        "",
        "#3.0.0 or later",
        "set {_packetgroup}'s deu glow color to green"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActiveGlowColor extends SimplePropertyExpression<Object, Color> {
    static {
        register(ExprActiveGlowColor.class, Color.class, "deu glow[ing] colo[u]r [override]", "activegroups/activeparts/multipartfilters");
    }

    @Override
    public Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    @Nullable
    public Color convert(Object object) {
        if (object instanceof ActiveGroup group){
            if (group.getGlowColor() != null){
                return ColorRGB.fromBukkitColor(group.getGlowColor());
            }
            return null;
        }
        else if (object instanceof ActivePart part){
            if (part.getGlowColor() != null){
                return ColorRGB.fromBukkitColor(part.getGlowColor());
            }
            else if (!part.isDisplay()){
                Skript.error("You can only get the glow color of a DISPLAY active part");
            }
            return null;
        }

        Skript.error("You can not get the glow color of a part filter", ErrorQuality.SEMANTIC_ERROR);
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
