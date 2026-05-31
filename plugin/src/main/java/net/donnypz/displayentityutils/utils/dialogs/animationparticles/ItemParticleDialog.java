package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;

import java.util.Collection;
import java.util.List;

class ItemParticleDialog extends ParticleDialog {

    private static final String ITEM_ID = "deu_particle_builder_item";

    ItemParticleDialog(Collection<FramePoint> framePoints) {
        super(Component.text("Item Animation Particle"),
                List.of(DialogInput.text(ITEM_ID, Component.text("Item Id")).build()),
                framePoints);
    }

    @Override
    ParticleDialog create(Collection<FramePoint> framePoints) {
        return new ItemParticleDialog(framePoints);
    }

    @Override
    protected DialogActionCallback buildConfirmCallback(Collection<FramePoint> framePoints) {
        return (view, audience) -> {
            String itemId = view.getText(ITEM_ID);
            ItemType itemType = Registry.ITEM.get(Key.key("minecraft", itemId));
            if (itemType == null){
                audience.sendMessage(Component.text("Failed to use item with the given id: "+itemId, NamedTextColor.RED));
                return;
            }
            this.buildParticle(view, audience, Particle.ITEM, itemType.createItemStack(), framePoints);
        };
    }
}
