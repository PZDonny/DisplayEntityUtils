package net.donnypz.displayentityutils.skript.group.activegroup.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group Location")
@Description("Get the location of an active group")
@Examples({"set {_location} to {_spawnedgroup}'s deu location",
            "",
            "#3.4.3 and earlier",
            "set {_location} to {_packetgroup}'s true location"})
@Since("2.6.2, 3.0.0 (Packet)")
public class ExprActiveGroupLocation extends SimplePropertyExpression<ActiveGroup, Location> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveGroupLocation.class, Location.class)
                        .addPatterns(getPatterns("deu [true] location", "activegroups"))
                        .supplier(ExprActiveGroupLocation::new)
                        .build()
        );
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
