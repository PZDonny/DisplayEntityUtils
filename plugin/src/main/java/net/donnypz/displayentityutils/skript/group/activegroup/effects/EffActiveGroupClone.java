package net.donnypz.displayentityutils.skript.group.activegroup.effects;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Clone Active Group")
@Description("Create a clone of an active group")
@Examples({
        "deu clone {_activegroup}",
        "deu clone {_activegroup} at {_location}",
        "deu clone {_activegroup} at {_location} and store the result in {_clonegroup}",
        "",
        "#3.4.3 and earlier",
        "set {_clonegroup} to a clone of {_activegroup}",
        "set {_clonegroup} to a clone of {_activegroup} at {_location}"
})
@Since("2.6.2, 3.3.1 (Packet)")
public class EffActiveGroupClone extends Effect {

    Expression<ActiveGroup> group;
    Expression<Location> location;
    private Expression<?> store;


    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupClone.class)
                        .addPattern("deu clone %activegroup% [loc:at %-location%] [store:[and] store[d] [it |the result] in %-objects%]")
                        .supplier(EffActiveGroupClone::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        if (parseResult.hasTag("loc")){
            location = (Expression<Location>) expressions[1];
        }
        if (parseResult.hasTag("store")){
            this.store = expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null) return;
        Location loc = location != null ? location.getSingle(event) : g.getLocation();
        ActiveGroup clone = g.clone(loc);

        if (this.store != null){
            store.change(event, new ActiveGroup[]{clone}, Changer.ChangeMode.SET);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "clone active group "+group.toString(event, debug);
    }
}
