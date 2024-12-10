package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;

@ApiStatus.Internal
public class ItemStackAnimationParticle extends AnimationParticle implements Serializable {

    byte[] itemStackAsBytes;
    transient ItemStack itemStack;

    @Serial
    private static final long serialVersionUID = 99L;


    ItemStackAnimationParticle(AnimationParticleBuilder builder, ItemStack itemStack) {
        super(builder, Particle.ITEM);
        updateItem(itemStack);
    }
    @ApiStatus.Internal
    public ItemStackAnimationParticle() {}

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, itemStack);
    }

    @Override
    protected void initalize() {
        itemStack = ItemStack.deserializeBytes(itemStackAsBytes);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Item: "+itemStack.getType().name(), AnimationParticleBuilder.Step.ITEM);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.ITEM){
            updateItem(builder.data());
            return true;
        }
        return false;
    }

    private void updateItem(ItemStack itemStack){
        this.itemStack = itemStack;
        this.itemStackAsBytes = itemStack.serializeAsBytes();
    }
}
