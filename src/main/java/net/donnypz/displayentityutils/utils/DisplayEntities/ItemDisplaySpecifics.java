package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.*;

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

    ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return itemDisplayTransform;
    }

    ItemStack getItemStack(){
        if (itemStack == null){
            return null;
        }
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
}
