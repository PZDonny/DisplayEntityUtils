package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

final class ItemDisplaySpecifics extends DisplayEntitySpecifics implements Serializable {
    @Serial
    private static final long serialVersionUID = 99L;
    private final ItemDisplay.ItemDisplayTransform itemDisplayTransform;
    byte[] itemStack;

    ItemDisplaySpecifics(ItemDisplay itemDisplay) {
        super(itemDisplay);
        this.itemDisplayTransform = itemDisplay.getItemDisplayTransform();
        this.itemStack = itemDisplay.getItemStack().serializeAsBytes();
    }

    ItemDisplaySpecifics(PacketDisplayEntityPart part) {
        super(part);
        this.itemDisplayTransform = part.getAttributeContainer().getAttributeOrDefault(DisplayAttributes.ItemDisplay.ITEM_DISPLAY_TRANSFORM, ItemDisplay.ItemDisplayTransform.NONE);
        ItemStack item = part.getAttributeContainer().getAttributeOrDefault(DisplayAttributes.ItemDisplay.ITEMSTACK, new ItemStack(Material.AIR));
        this.itemStack = item.serializeAsBytes();
    }

    ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return itemDisplayTransform;
    }

    ItemStack getItemStack(){
        if (itemStack != null){
            //Old Method of serialization (Before Deprecation)
            try{
                ByteArrayInputStream byteIn = new ByteArrayInputStream(itemStack);
                BukkitObjectInputStream bukkitIn = new BukkitObjectInputStream(byteIn);
                return (ItemStack) bukkitIn.readObject();
            }
            //New Method of serialization (ItemStack#serializeAsBytes())
            catch (IOException e) {
                return ItemStack.deserializeBytes(itemStack);
            }
            catch (ClassNotFoundException e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected void applyToAttributeContainer(PacketAttributeContainer attributeContainer) {
        attributeContainer.setAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK, getItemStack())
                .setAttribute(DisplayAttributes.ItemDisplay.ITEM_DISPLAY_TRANSFORM, itemDisplayTransform);
    }
}
