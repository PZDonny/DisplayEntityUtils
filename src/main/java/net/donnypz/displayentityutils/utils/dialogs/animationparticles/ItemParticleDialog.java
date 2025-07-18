package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;

import java.util.List;

class ItemParticleDialog extends ParticleDialog {

    private static final String ITEM_ID = "deu_particle_builder_item";

    ItemParticleDialog() {
        super(Component.text("Block Animation Particle"), List.of(DialogInput.text(ITEM_ID, Component.text("Item Id")).build()));
    }

    @Override
    protected DialogActionCallback buildConfirmCallback() {
        return (view, audience) -> {
            String itemId = view.getText(ITEM_ID);
            ItemType itemType = Registry.ITEM.get(Key.key("minecraft", itemId));
            if (itemType == null){
                audience.sendMessage(Component.text("Failed to use item with the given id: "+itemId, NamedTextColor.RED));
                return;
            }
            this.buildParticle(view, audience, Particle.ITEM, itemType.createItemStack());
        };
    }
}
