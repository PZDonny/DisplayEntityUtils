package net.donnypz.displayentityutils.skript.group.packetgroup.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Persistent Packet Group Id")
@Description("Get the id of a persistent packet-based group, which can be used to get the group at the later time, even in different sessions.")
@Examples({"set {id} to {_packetgroup}'s persistent id"})
@Since("3.3.4")
public class ExprPersistentPacketGroupId extends SimplePropertyExpression<PacketDisplayEntityGroup, String> {


    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprPersistentPacketGroupId.class, String.class)
                        .addPatterns(getPatterns("persistent [global] id", "packetgroup"))
                        .supplier(ExprPersistentPacketGroupId::new)
                        .build()
        );
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
