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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Toggle Spawned Animation Texture Changes")
@Description("Toggle whether textures should be updated during an animation. (Player Head Changes during animation, etc.)")
@Examples({"set {_spawnedanimation} to allow texture changes",
            "set {_spawnedanimation} to not allow texture changes"})
@Since("3.3.1")
public class EffSpawnedAnimTextureChanges extends Effect {
    static {
        Skript.registerEffect(EffSpawnedAnimTextureChanges.class,"(make|set) %spawnedanimations% [to] [:not] allow texture change[s]");
    }

    Expression<SpawnedDisplayAnimation> object;
    boolean enabled;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = (Expression<SpawnedDisplayAnimation>) expressions[0];
        enabled = !parseResult.hasTag("not");
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayAnimation[] animations = object.getArray(event);
        if (animations == null) return;
        for (SpawnedDisplayAnimation a : animations){
            if (a != null){
                a.allowDataChanges(enabled);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawned animation texture changes: "+object.toString(event, debug);
    }
}
