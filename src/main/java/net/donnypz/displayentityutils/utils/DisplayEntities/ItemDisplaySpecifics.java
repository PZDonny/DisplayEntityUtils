package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;

final class ItemDisplaySpecifics extends DisplayEntitySpecifics implements Serializable {
    @Serial
    private static final long serialVersionUID = 99L;
    private final ItemDisplay.ItemDisplayTransform itemDisplayTransform;
    byte[] itemStack;

    ItemDisplaySpecifics(ItemDisplay itemDisplay) {
        super(itemDisplay);
        this.itemDisplayTransform = itemDisplay.getItemDisplayTransform();
        if (itemDisplay.getItemStack() != null){
            try{
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream bukkitStream = new BukkitObjectOutputStream(byteStream);
                bukkitStream.writeObject(itemDisplay.getItemStack());
                this.itemStack = byteStream.toByteArray();
            }
             catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return itemDisplayTransform;
    }

    ItemStack getItemStack(){
        if (itemStack == null){
            return null;
        }
        try{
            ByteArrayInputStream byteIn = new ByteArrayInputStream(itemStack);
            BukkitObjectInputStream bukkitIn = new BukkitObjectInputStream(byteIn);
            return (ItemStack) bukkitIn.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
