package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Part Selection Filter Part Tags")
@Description("Filter part tags in a part selection")
@Examples({"#Filter parts with the tag \"head\" but exclude if they have \"head.eye\"",
        "add filter to {_selection} with rig bone \"head\" and without rig bone \"head.eye\"",
        "",
        "#Filter all parts except ones with the rig bone \"left_arm.glove\"",
        "add filter to {_selection} without rig bone \"left_arm.glove\""})
@Since("2.6.2")
public class EffPartSelectionFilterRigBones extends Effect {
    static {
        Skript.registerEffect(EffPartSelectionFilterRigBones.class,"add filter to %multipartselection% [in:with [rig] bone[s] %strings%] [ex:[and ]without [rig] bones[s] %strings%]");
    }

    Expression<MultiPartSelection> selection;
    Expression<String> includedRigBones;
    Expression<String> excludedRigBones;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        selection = (Expression<MultiPartSelection>) expressions[0];
        if (parseResult.hasTag("in")){
            includedRigBones = (Expression<String>) expressions[1];
        }
        if (parseResult.hasTag("ex")){
            excludedRigBones = (Expression<String>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        MultiPartSelection sel = selection.getSingle(event);
        if (sel == null || (includedRigBones == null && excludedRigBones == null)){
            return;
        }
        PartFilter builder = new PartFilter();
        if (includedRigBones != null){
            String[] bones = includedRigBones.getArray(event);
            if (bones != null){
                for (String bone : bones){
                    builder.includeRigBone(bone);
                }
            }
        }

        if (excludedRigBones != null){
            String[] bones = excludedRigBones.getArray(event);
            if (bones != null){
                for (String bone : bones){
                    builder.excludeRigBone(bone);
                }
            }
        }
        sel.applyFilter(builder, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add multi part selection bone rig filter: "+selection.toString(event, debug);
    }
}
