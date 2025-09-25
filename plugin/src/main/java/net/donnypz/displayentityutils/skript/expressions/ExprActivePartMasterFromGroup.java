package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.jetbrains.annotations.Nullable;

@Name("Parent/Master Part of Active Group")
@Description("Get the Parent/Master part of an active group, that all other parts are passengers of")
@Examples({"set {_masterpart} to {_spawnedgroup}'s master part"})
@Since("3.3.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartMasterFromGroup extends SimplePropertyExpression<ActiveGroup<?>, ActivePart> {

    static {
        register(ExprActivePartMasterFromGroup.class, ActivePart.class, "(parent|master) [active] part", "activegroups");
    }

    @Override
    public Class<? extends ActivePart> getReturnType() {
        return ActivePart.class;
    }

    @Override
    @Nullable
    public ActivePart convert(ActiveGroup<?> group) {
        if (group == null){
            return null;
        }
        return group.getMasterPart();
    }

    @Override
    protected String getPropertyName() {
        return "master part";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
