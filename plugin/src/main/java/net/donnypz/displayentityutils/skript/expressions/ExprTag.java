package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Group/Animation Tag")
@Description("Get or set the tag of a group/animation. Only the tag of a active group/animation can be changed")
@Examples({"reset {_spawnedgroup}'s tag",
            "set {_savedgrouptag} to {_savedgroup}'s tag",
            "",
            "set {_animation}'s tag to \"newTag\"",
            "",
            "#3.0.0 and later",
            "set {_packetgrouptag} to {_packetgroup}'s tag",
            "",
            "#3.2.1 and later",
            "set {_framepointtag} to {_framepoint}'s tag"})
@Since("2.6.2")
public class ExprTag extends SimplePropertyExpression<Object, String> {
    static {
        register(ExprTag.class, String.class, "[the] tag", "activegroup/savedgroup/animation/framepoint");
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    @Nullable
    public String convert(Object obj) {
        if (obj instanceof ActiveGroup g){
            return g.getTag();
        }
        else if (obj instanceof DisplayEntityGroup g){
            return g.getTag();
        }
        else if (obj instanceof SpawnedDisplayAnimation a){
            return a.getAnimationTag();
        }
        else if (obj instanceof DisplayAnimation a){
            return a.getAnimationTag();
        }
        else if (obj instanceof FramePoint f){
            return f.getTag();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "tag";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Object o = getExpr().getSingle(event);

        if (o instanceof SpawnedDisplayEntityGroup g){
            switch (mode) {
                case SET -> {
                    if (delta == null){
                        return;
                    }
                    String newTag = (String) delta[0];
                    g.setTag(newTag);
                }
                case RESET -> {
                    g.setTag(null);
                }
            }
        }
        else if (o instanceof SpawnedDisplayAnimation a){
            switch (mode) {
                case SET -> {
                    if (delta == null){
                        return;
                    }
                    String newTag = (String) delta[0];
                    a.setAnimationTag(newTag);
                }
                case RESET -> {
                    a.setAnimationTag(null);
                }
            }
        }
        Skript.error("You can only set the tag of a spawned group or animation", ErrorQuality.SEMANTIC_ERROR);

    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
