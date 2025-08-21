package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.inventory.ItemStack;

public class ItemStackDisplayAttribute extends DisplayAttribute<ItemStack, com.github.retrooper.packetevents.protocol.item.ItemStack> {

    ItemStackDisplayAttribute(int index) {
        super(index, ItemStack.class, com.github.retrooper.packetevents.protocol.item.ItemStack.class, EntityDataTypes.ITEMSTACK);
    }

    @Override
    public com.github.retrooper.packetevents.protocol.item.ItemStack getOutputValue(ItemStack value) {
        return SpigotConversionUtil.fromBukkitItemStack(value);
    }
}
