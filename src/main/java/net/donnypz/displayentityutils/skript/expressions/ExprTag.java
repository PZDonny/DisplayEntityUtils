package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Group/Animation Tag")
@Description("Get or set the tag of a group/animation")
@Examples({"#Only the tag of a spawned group/animation can be changed",
            "reset {_spawnedgroup}'s tag",
            "set {_savedgrouptag} to {_savedgroup}'s tag",
            "",
            "set {_spawnedanimation}'s tag to \"newTag\"",
            "set {_savedanimationtag}'s tag to {_savedanimation}'s tag"})
@Since("2.6.2")
public class ExprTag extends SimplePropertyExpression<Object, String> {
    static {
        register(ExprTag.class, String.class, "[the] tag", "spawnedgroup/savedgroup/spawnedanimation/savedanimation");
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    @Nullable
    public String convert(Object obj) {
        if (obj instanceof SpawnedDisplayEntityGroup g){
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

    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
