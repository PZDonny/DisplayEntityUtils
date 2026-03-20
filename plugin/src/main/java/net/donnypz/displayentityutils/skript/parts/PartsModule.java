package net.donnypz.displayentityutils.skript.parts;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.parts.conditions.CondInteractionHasCommands;
import net.donnypz.displayentityutils.skript.parts.conditions.CondInteractionIsResponsive;
import net.donnypz.displayentityutils.skript.parts.conditions.CondPartIsDisplayPart;
import net.donnypz.displayentityutils.skript.parts.conditions.CondPartIsMaster;
import net.donnypz.displayentityutils.skript.parts.effects.EffActivePartTag;
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

                EffActivePartTag::register,

                ExprActivePartEntityId::register,
                ExprActivePartMasterFromGroup::register,
                ExprActivePartsFromActive::register,
                ExprActivePartTags::register,
                ExprActivePartTransLocation::register,
                ExprActivePartType::register,
                ExprActivePartUUID::register,
                ExprSpawnedPartAsEntity::register,
                ExprSpawnedPartOfEntity::register,

                ExprBlockDisplayBlock::register,
                ExprInteractionDimensions::register,
                ExprItemDisplayItem::register,
                ExprItemDisplayTransform::register


        );
    }

    @Override
    public String name() {
        return "parts";
    }
}
