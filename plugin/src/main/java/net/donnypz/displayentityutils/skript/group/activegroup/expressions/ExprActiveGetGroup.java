package net.donnypz.displayentityutils.skript.group.activegroup.expressions;

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
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group From Part/Filter/Entity")
@Description("Get the active group of a active part, part filter, or an eligible part entity")
@Examples({"set {_activegroup} to {_activepart}'s active group",
            "",
            "set {_activegroup} to {_partfilter}'s active group",
            "",
            "set {_activegroup} to {_displayentity}'s active group"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActiveGetGroup extends SimplePropertyExpression<Object, ActiveGroup> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveGetGroup.class, ActiveGroup.class)
                        .addPatterns(getPatterns("active[ |-]group", "activeparts/multipartfilters/entities"))
                        .supplier(ExprActiveGetGroup::new)
                        .build()
        );
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
            GroupResult result = DisplayGroupManager.getOrCreateSpawnedGroup(display);
            if (result == null) return null;
            return result.group();
        }
        else if (obj instanceof Entity entity){
            return DisplayGroupManager.getSpawnedGroup(entity, DisplayConfig.getMaximumInteractionSearchRange());
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
