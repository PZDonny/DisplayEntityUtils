package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.jetbrains.annotations.Nullable;

@Name("Persistent Packet Group Id")
@Description("Get the id of a persistent packet group, which can be used to get the group at the later time, even in different sessions.")
@Examples({"set {id} to {_packetgroup}'s persistent id"})
@Since("3.3.4")
public class ExprPersistentPacketGroupId extends SimplePropertyExpression<PacketDisplayEntityGroup, String> {

    static {
        register(ExprPersistentPacketGroupId.class, String.class, "persistent [global] id", "packetgroup");
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    @Nullable
    public String convert(PacketDisplayEntityGroup pdeg) {
        return pdeg.getPersistentGlobalId();
    }

    @Override
    protected String getPropertyName() {
        return "persistent id";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
