package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Add/Remove Part Tag")
@Description("Add/Remove a part tag from an active part")
@Examples({"add part tag \"newtag\" to {_spawnedpart}",
        "remove tag \"i_dont_want_this_tag\" from {_activepart}",})
@Since({"2.6.2, 3.3.4 (Packet-Parts)"})
@DocumentationId("EffSpawnedPartTag")
public class EffActivePartTag extends Effect {
    static {
        Skript.registerEffect(EffActivePartTag.class,"(:add|remove) [part][-| ]tag %strings% (to|from) %activeparts%");
    }

    Expression<String> tags;
    Expression<ActivePart> part;
    boolean add;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        tags = (Expression<String>) expressions[0];
        part = (Expression<ActivePart>) expressions[1];
        add = parseResult.hasTag("add");
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActivePart[] parts = part.getArray(event);
        String[] t = tags.getArray(event);
        if (parts == null || t == null){
            return;
        }
        for (ActivePart part : parts){
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
