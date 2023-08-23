package com.pzdonny.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.BlockDisplay;

import java.io.Serial;
import java.io.Serializable;

public final class BlockDisplaySpecifics extends DisplayEntitySpecifics implements Serializable {

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
}
