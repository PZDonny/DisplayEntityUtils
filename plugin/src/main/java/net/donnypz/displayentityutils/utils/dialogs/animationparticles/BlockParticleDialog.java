package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;

import java.util.Collection;
import java.util.List;

class BlockParticleDialog extends ParticleDialog {

    private static final String BLOCK_ID = "deu_particle_builder_block";

    BlockParticleDialog(Collection<FramePoint> framePoints) {
        super(Component.text("Block Animation Particle"),
                List.of(DialogInput.text(BLOCK_ID, Component.text("Block Id")).build()),
                framePoints);
    }

    @Override
    ParticleDialog create(Collection<FramePoint> framePoints) {
        return new BlockParticleDialog(framePoints);
    }

    @Override
    protected DialogActionCallback buildConfirmCallback(Collection<FramePoint> framePoints) {
        return (view, audience) -> {
            String blockId = view.getText(BLOCK_ID);
            BlockType blockType = Registry.BLOCK.get(Key.key("minecraft", blockId));
            if (blockType == null){
                audience.sendMessage(Component.text("Failed to use block with the given id: "+blockId, NamedTextColor.RED));
                return;
            }
            this.buildParticle(view, audience, Particle.BLOCK, blockType.createBlockData(), framePoints);
        };
    }
}
