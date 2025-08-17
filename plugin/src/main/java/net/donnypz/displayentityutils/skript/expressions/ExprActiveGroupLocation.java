package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Location")
@Description("Get the location of an active group")
@Examples({"set {_location} to {_spawnedgroup}'s true location",
            "",
            "#3.0.0 and later",
            "set {_location to {_packetgroup}'s true location"})
@Since("2.6.2")
public class ExprActiveGroupLocation extends SimplePropertyExpression<ActiveGroup, Location> {

    static {
        register(ExprActiveGroupLocation.class, Location.class, "[the] true location", "activegroup");
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    @Nullable
    public Location convert(ActiveGroup group) {
        if (group == null){
            return null;
        }
        return group.getLocation();
    }

    @Override
    protected String getPropertyName() {
        return "true location";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
