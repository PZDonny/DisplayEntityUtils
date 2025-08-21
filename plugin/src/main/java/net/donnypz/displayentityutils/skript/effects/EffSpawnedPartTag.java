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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Add/Remove Part Tag")
@Description("Add/Remove a part tag from a spawned part")
@Examples({"add part tag \"newtag\" to {_spawnedpart}", "remove tag \"i_dont_want_this_tag\" from {_spawnedpart}"})
@Since("2.6.2")
public class EffSpawnedPartTag extends Effect {
    static {
        Skript.registerEffect(EffSpawnedPartTag.class,"(:add|remove) [part][-| ]tag %strings% (to|from) %spawnedparts%");
    }

    Expression<String> tags;
    Expression<SpawnedDisplayEntityPart> part;
    boolean add;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        tags = (Expression<String>) expressions[0];
        part = (Expression<SpawnedDisplayEntityPart>) expressions[1];
        add = parseResult.hasTag("add");
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityPart[] parts = part.getArray(event);
        String[] t = tags.getArray(event);
        if (parts == null || t == null){
            return;
        }
        for (SpawnedDisplayEntityPart part : parts){
            for (String tag : t){
                if (tag == null) continue;
                if (add){
                    part.addTag(tag);
                }
                else{
                    part.removeTag(tag);
                }
            }
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add/remove part tag: "+part.toString(event, debug);
    }
}
