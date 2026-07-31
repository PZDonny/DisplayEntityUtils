package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;

@ApiStatus.Internal
class ItemStackAnimationParticle extends AnimationParticle<ItemStack> implements Serializable {

    byte[] itemStackAsBytes;
    transient ItemStack itemStack;

    @Serial
    private static final long serialVersionUID = 99L;

    ItemStackAnimationParticle(AnimationParticleBuilder builder, ItemStack itemStack) {
        super(builder, VersionUtils.getItemParticle(), itemStack);
    }
    @ApiStatus.Internal
    public ItemStackAnimationParticle() {}

    @Override
    AnimationParticleBuilder.Step getStep() {
        return AnimationParticleBuilder.Step.ITEM;
    }

    @Override
    ItemStack getSpawnData() {
        return itemStack;
    }

    @Override
    void update(ItemStack data) {
        this.itemStack = data;
        this.itemStackAsBytes = itemStack.serializeAsBytes();
    }

    @Override
    boolean canUseData() {
        return true;
    }

    @Override
    protected void initalize() {
        itemStack = ItemStack.deserializeBytes(itemStackAsBytes);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Item: "+itemStack.getType().name());
    }
}
