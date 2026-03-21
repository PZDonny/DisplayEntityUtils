package net.donnypz.displayentityutils.skript.animation.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Toggle Animation Texture Changes")
@Description("Toggle whether textures should be updated during an animation. (Player Head Changes during animation, etc.)")
@Examples({
        "enable texture changes for {_animation}",
        "disable texture changes for {_animation}",
        "",
        "#3.4.3 and earlier",
        "set {_animation} to allow texture changes",
        "set {_animation} to not allow texture changes"
})
@Since("3.3.1")
public class EffAnimationTextureChanges extends Effect {

    Expression<SpawnedDisplayAnimation> object;
    boolean enabled;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffAnimationTextureChanges.class)
                        .addPattern("(:dis|en)able texture change[s] for %deuanimations%")
                        .supplier(EffAnimationTextureChanges::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = (Expression<SpawnedDisplayAnimation>) expressions[0];
        enabled = !parseResult.hasTag("dis");
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayAnimation[] animations = object.getArray(event);
        if (animations == null) return;
        for (SpawnedDisplayAnimation a : animations){
            if (a != null){
                a.allowTextureChanges(enabled);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "animation texture changes: "+object.toString(event, debug);
    }
}
