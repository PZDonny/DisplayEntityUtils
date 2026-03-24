package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Master/Parent Part of Active Group")
@Description("Get the master/parent part of an active group, which all other parts are passengers of")
@Examples({"set {_masterpart} to {_spawnedgroup}'s master part"})
@Since("3.3.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartMasterFromGroup extends SimplePropertyExpression<ActiveGroup<?>, ActivePart> {


    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActivePartMasterFromGroup.class, ActivePart.class)
                        .addPatterns(getPatterns("(parent|master) [active] part", "activegroups"))
                        .supplier(ExprActivePartMasterFromGroup::new)
                        .build()
        );
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
