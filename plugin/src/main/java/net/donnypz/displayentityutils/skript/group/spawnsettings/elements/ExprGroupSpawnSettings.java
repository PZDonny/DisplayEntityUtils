package net.donnypz.displayentityutils.skript.group.spawnsettings.elements;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.events.PreGroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Create Group Spawn Settings")
@Description("Create group spawn settings that can be applied to a saved group when spawning it")
@Examples({"set {_settings} to new group spawn settings"})
@Since("2.6.2")
public class ExprGroupSpawnSettings extends SimpleExpression<GroupSpawnSettings> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprGroupSpawnSettings.class, GroupSpawnSettings.class)
                        .addPattern("[a] [new] [group] spawn setting[s]")
                        .supplier(ExprGroupSpawnSettings::new)
                        .build()
        );
    }

    @Override
    protected GroupSpawnSettings[] get(Event event) {
        return new GroupSpawnSettings[]{new GroupSpawnSettings()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends GroupSpawnSettings> getReturnType() {
        return GroupSpawnSettings.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "a new group spawn setting";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return CollectionUtils.array(GroupSpawnSettings.class);
        return null;
    }

    @Override
    public void change(final Event e, final @Nullable Object[] delta, final Changer.ChangeMode mode) {
        if (e instanceof PreGroupSpawnedEvent ev && mode == Changer.ChangeMode.SET){
            ev.setGroupSpawnSettings((GroupSpawnSettings) delta[0]);
        }
    }
}
