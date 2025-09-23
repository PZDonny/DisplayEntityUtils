package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.Nullable;

@Name("Translated Location of Active Part / Entity")
@Description("Get the location relative to a active part's true location, based off of its translation.")
@Examples({"set {_loc} to {_spawnedpart}'s translated location",
            "set {_loc} to {_displayentity}'s translated location"})
@Since("3.1.2, 3.3.2 (Plural)")
public class ExprActivePartTransLocation extends SimplePropertyExpression<Object, Location> {

    static {
        register(ExprActivePartTransLocation.class, Location.class, "translated location", "activeparts/entities");
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    @Nullable
    public Location convert(Object o) {
        if (o instanceof SpawnedDisplayEntityPart part){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                return part.getLocation();
            }
            else{
                return DisplayUtils.getModelLocation((Display) part.getEntity());
            }
        }
        else if (o instanceof PacketDisplayEntityPart part){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                return part.getLocation();
            }
            else{
                return DisplayUtils.getModelLocation(part);
            }
        }
        else if (o instanceof Display d){
            return DisplayUtils.getModelLocation(d);
        }
        else if (o instanceof Interaction i){
            return i.getLocation();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "translated location";
    }

}
