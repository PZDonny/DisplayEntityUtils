package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;

import java.io.Serial;
import java.io.Serializable;

final class BlockDisplaySpecifics extends DisplayEntitySpecifics implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;
    private final String blockData;

    BlockDisplaySpecifics(BlockDisplay display) {
        super(display);
        blockData = display.getBlock().getAsString();
    }

    String getBlockData() {
        return blockData;
    }

    @Override
    protected void applyToAttributeContainer(PacketAttributeContainer attributeContainer) {
        attributeContainer.setAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE, Bukkit.createBlockData(blockData));
    }
}
