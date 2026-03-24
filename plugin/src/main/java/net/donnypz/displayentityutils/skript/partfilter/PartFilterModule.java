package net.donnypz.displayentityutils.skript.partfilter;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.partfilter.effects.EffPartFilterItemsBlocks;
import net.donnypz.displayentityutils.skript.partfilter.effects.EffPartSelectionFilterPartTypes;
import net.donnypz.displayentityutils.skript.partfilter.effects.EffPartSelectionFilterTags;
import net.donnypz.displayentityutils.skript.partfilter.effects.EffPartSelectionResetRefreshRemove;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class PartFilterModule implements AddonModule {

    @Override
    public void load(SkriptAddon addon) {
        SkriptUtil.registerModules(addon.syntaxRegistry(),
                EffPartFilterItemsBlocks::register,
                EffPartSelectionFilterPartTypes::register,
                EffPartSelectionFilterTags::register,
                EffPartSelectionResetRefreshRemove::register
        );
    }

    @Override
    public String name() {
        return "partfilter";
    }
}
