package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawn Packet-Based Group")
@Description("Spawn a saved group at a location, using packets")
@Examples({"set {_packetgroup} to packet based {_savedgroup} spawned at {_location}",
        "",
        "#(3.3.4+) Spawn packet based group that will persist after restarts",
        "set {_packetgroup} to persistent packet based {_savedgroup} spawned at {_location}",
        "",
        "#(3.3.6+) Spawn packet based group with spawn settings, changing its properties",
        "set {_packetgroup} to packet based {_savedgroup} spawned at {_location} with {_groupspawnsettings}"})
@Since("3.0.0, 3.3.4 (Persistent), 3.3.6 (GroupSpawnSettings)")
@DocumentationId("ExprSavedGroupToPacket")
public class ExprSpawnPacketGroup extends SimpleExpression<PacketDisplayEntityGroup> {

    static{
        Skript.registerExpression(ExprSpawnPacketGroup.class, PacketDisplayEntityGroup.class, ExpressionType.COMBINED, "[:persistent] packet [based] %savedgroup% spawned at %location% [w:with %-groupspawnsetting%]");
    }

    private Expression<DisplayEntityGroup> savedGroup;
    private Expression<Location> location;
    boolean persistent;
    private Expression<GroupSpawnSettings> groupSpawnSettings = null;

    @Override
    protected PacketDisplayEntityGroup[] get(Event event) {
        DisplayEntityGroup saved = savedGroup.getSingle(event);
        if (saved == null) return new PacketDisplayEntityGroup[0];
        Location loc = location.getSingle(event);
        if (loc == null) return new PacketDisplayEntityGroup[0];
        GroupSpawnSettings settings;
        if (groupSpawnSettings != null){
            settings = groupSpawnSettings.getSingle(event);
            if (settings == null){
                settings = new GroupSpawnSettings().visibleByDefault(false, null);
            }
        }
        else{
            settings = new GroupSpawnSettings().visibleByDefault(false, null);
        }
        if (persistent){
            return new PacketDisplayEntityGroup[]{DisplayGroupManager.addPersistentPacketGroup(loc, saved, settings, GroupSpawnedEvent.SpawnReason.SKRIPT)};
        }
        else{
            return new PacketDisplayEntityGroup[]{saved.createPacketGroup(loc, GroupSpawnedEvent.SpawnReason.SKRIPT, true, settings)};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends PacketDisplayEntityGroup> getReturnType() {
        return PacketDisplayEntityGroup.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (persistent ? "persistent" : "")+"packet based " + savedGroup.toString(event, debug)+" spawned at " + location.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        savedGroup = (Expression<DisplayEntityGroup>) expressions[0];
        location = (Expression<Location>) expressions[1];
        persistent = parseResult.hasTag("persistent");
        if (parseResult.hasTag("w")){
            groupSpawnSettings = (Expression<GroupSpawnSettings>) expressions[2];
        }
        return true;
    }
}
