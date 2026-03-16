package net.donnypz.displayentityutils.skript.group.spawnsettings.elements;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Group Spawn Settings Billboard")
@Description("Set the billboard property of a group spawn setting")
@Examples({
        "#Set billboard (Before 2.7.7)",
        "make {_setting}'s billboard to vertical",
        "make {_setting}'s billboard to center for parts with tag \"pivotpart\"",
        "",
        "#Set billboard (2.7.7+)",
        "deu set {_setting}'s billboard as vertical",
        "make {_setting}'s billboard as center"})
@Since("2.6.2")
public class EffGroupSpawnSettingBillboard extends Effect {

    Expression<GroupSpawnSettings> settings;
    Expression<Display.Billboard> billboard;
    Expression<String> tags;


    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffGroupSpawnSettingBillboard.class)
                        .addPattern("(make|deu set) %groupspawnsetting%['s] billboard (to|as) %billboard% [tags:for parts with tag[s] %-strings%]")
                        .supplier(EffGroupSpawnSettingBillboard::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        settings = (Expression<GroupSpawnSettings>) expressions[0];
        billboard = (Expression<Display.Billboard>) expressions[1];
        if (parseResult.hasTag("tags")){
            tags = (Expression<String>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        GroupSpawnSettings s = settings.getSingle(event);
        Display.Billboard b = billboard.getSingle(event);
        if (tags == null){
            s.addBillboard(b, null);
        }
        else{
            for (String tag : tags.getArray(event)){
                s.addBillboard(b, tag);
            }
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "billboard spawn settings: "+settings.toString(event, debug);
    }
}
