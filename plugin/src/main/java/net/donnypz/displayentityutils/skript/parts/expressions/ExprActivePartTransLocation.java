package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Translated Location of Active Part / Entity")
@Description("Get the location relative to a active part's true location, based off of its translation.")
@Examples({"set {_loc} to {_activepart}'s translated location",
            "set {_loc} to {_displayentity}'s translated location"})
@Since("3.1.2, 3.3.2 (Plural)")
public class ExprActivePartTransLocation extends SimplePropertyExpression<Object, Location> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActivePartTransLocation.class, Location.class)
                        .addPatterns(getPatterns("translated location", "activeparts/displays"))
                        .supplier(ExprActivePartTransLocation::new)
                        .build()
        );
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    @Nullable
    public Location convert(Object o) {
        if (o instanceof ActivePart part){
            if (part.isDisplay()){
                return DisplayUtils.getModelLocation(part);
            }
            else{
                return part.getLocation();
            }
        }
        else if (o instanceof Display d){
            return DisplayUtils.getModelLocation(d);
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "translated location";
    }

}
