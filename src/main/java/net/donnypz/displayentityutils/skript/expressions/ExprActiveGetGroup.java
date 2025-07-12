package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.jetbrains.annotations.Nullable;

@Name("Active Group From (Packet) Part/Part Selection")
@Description("Get the active group of a active part or part selection")
@Examples({"set {_spawnedgroup} to {_spawnedpart}'s spawned group",
            "",
            "#3.0.0 and later",
            "set {_packetgroup} to {_packetpart}'s packet group",
            "set {_activegroup} to {_part}'s active group"})
@Since("2.6.2")
public class ExprActiveGetGroup extends SimplePropertyExpression<Object, ActiveGroup> {

    static {
        register(ExprActiveGetGroup.class, ActiveGroup.class, "[the] (active|spawned|packet)[ |-]group", "activepart/activepartselection");
    }

    @Override
    public Class<? extends ActiveGroup> getReturnType() {
        return ActiveGroup.class;
    }

    @Override
    @Nullable
    public ActiveGroup convert(Object spawned) {
        if (spawned instanceof ActivePart part){
            return part.getGroup();
        }
        else if (spawned instanceof ActivePartSelection sel){
            return sel.getGroup();
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
