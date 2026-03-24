package net.donnypz.displayentityutils.skript.parts;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.parts.conditions.*;
import net.donnypz.displayentityutils.skript.parts.effects.EffActivePartTag;
import net.donnypz.displayentityutils.skript.parts.effects.EffInteractionResponsive;
import net.donnypz.displayentityutils.skript.parts.effects.EffTextDisplaySeeThrough;
import net.donnypz.displayentityutils.skript.parts.effects.EffTextDisplayShadow;
import net.donnypz.displayentityutils.skript.parts.expressions.*;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class PartsModule implements AddonModule {

    @Override
    public void load(SkriptAddon addon) {
        SkriptUtil.registerModules(addon.syntaxRegistry(),
                CondInteractionHasCommands::register,
                CondInteractionIsResponsive::register,
                CondPartIsDisplayPart::register,
                CondPartIsMaster::register,
                CondTextDisplayIsShadowed::register,
                CondTextDisplaySeeThrough::register,

                EffActivePartTag::register,
                EffInteractionResponsive::register,
                EffTextDisplaySeeThrough::register,
                EffTextDisplayShadow::register,

                ExprActivePartEntityId::register,
                ExprActivePartMasterFromGroup::register,
                ExprActivePartsFromActive::register,
                ExprActivePartTags::register,
                ExprActivePartTransLocation::register,
                ExprActivePartType::register,
                ExprActivePartUUID::register,

                ExprBlockDisplayBlock::register,
                ExprInteractionDimensions::register,
                ExprItemDisplayItem::register,
                ExprItemDisplayTransform::register,

                ExprSpawnedPartAsEntity::register,
                ExprSpawnedPartOfEntity::register,

                ExprTextDisplayLineWidth::register,
                ExprTextDisplayOpacity::register,
                ExprTextDisplayText::register
        );
    }

    @Override
    public String name() {
        return "parts";
    }
}
