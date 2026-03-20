package net.donnypz.displayentityutils.skript.group.packetgroup;

import net.donnypz.displayentityutils.skript.SkriptUtil;
import net.donnypz.displayentityutils.skript.group.packetgroup.effects.EffPacketGroupAutoShow;
import net.donnypz.displayentityutils.skript.group.packetgroup.effects.EffPacketGroupPersistentUpdate;
import net.donnypz.displayentityutils.skript.group.packetgroup.expressions.ExprPersistentPacketGroupFromId;
import net.donnypz.displayentityutils.skript.group.packetgroup.expressions.ExprPersistentPacketGroupId;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class PacketGroupModule implements AddonModule {


    @Override
    public void load(SkriptAddon addon) {
        SkriptUtil.registerModules(addon.syntaxRegistry(),
                EffPacketGroupAutoShow::register,
                EffPacketGroupPersistentUpdate::register,

                ExprPersistentPacketGroupFromId::register,
                ExprPersistentPacketGroupId::register
        );
    }

    @Override
    public String name() {
        return "packetgroup";
    }
}
