package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.GroupResult;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.Nullable;

@Name("Active Group From Part/PartSelection/Entity")
@Description("Get the active group of a active part or part selection or an Display/Interaction entity")
@Examples({"set {_spawnedgroup} to {_spawnedpart}'s spawned group",
            "",
            "#3.0.0 and later",
            "set {_packetgroup} to {_packetpart}'s packet group",
            "set {_activegroup} to {_part}'s active group",
            "3.3.2 and later",
            "set {_spawnedgroup} to {_display}'s spawned group"})
@Since("2.6.2")
public class ExprActiveGetGroup extends SimplePropertyExpression<Object, ActiveGroup> {

    static {
        register(ExprActiveGetGroup.class, ActiveGroup.class, "(active|spawned|packet)[ |-]group", "activeparts/multipartselections/entities");
    }

    @Override
    public Class<? extends ActiveGroup> getReturnType() {
        return ActiveGroup.class;
    }

    @Override
    @Nullable
    public ActiveGroup<?> convert(Object obj) {
        if (obj instanceof ActivePart part){
            return part.getGroup();
        }
        else if (obj instanceof MultiPartSelection<?> sel){
            return sel.getGroup();
        }
        else if (obj instanceof Display display){
            GroupResult result = DisplayGroupManager.getSpawnedGroup(display, null);
            if (result == null) return null;
            return result.group();
        }
        else if (obj instanceof Interaction interaction){
            return DisplayGroupManager.getSpawnedGroup(interaction, DisplayConfig.getMaximumInteractionSearchRange());
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "activegroup";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
