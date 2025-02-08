package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Part's Tags")
@Description("Get all the tags of a spawned part")
@Examples({"set {_tags::*} to {_spawnedpart}'s part tags"})
@Since("2.6.2")
public class ExprSpawnedPartTags extends SimplePropertyExpression<SpawnedDisplayEntityPart, String[]> {

    static {
        register(ExprSpawnedPartTags.class, String[].class, "[the] [part] tags", "spawnedpart");
    }

    @Override
    public Class<? extends String[]> getReturnType() {
        return String[].class;
    }

    @Override
    @Nullable
    public String[] convert(SpawnedDisplayEntityPart part) {
        if (part == null){
            return null;
        }
        return part.getTags().toArray(new String[0]);
    }

    @Override
    protected String getPropertyName() {
        return "tags";
    }

    @Override
    public boolean isSingle() {
        return false;
    }
}
