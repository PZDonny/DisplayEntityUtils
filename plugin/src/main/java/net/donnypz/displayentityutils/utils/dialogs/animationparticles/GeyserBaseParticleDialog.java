package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;

import java.util.Collection;
import java.util.List;

class GeyserBaseParticleDialog extends ParticleDialog {

    private static final String GEYSER_PARTICLE = "deu_particle_builder_geyser_particle";
    private static final String WATER_BLOCKS = "deu_particle_builder_water_blocks";
    private static final String BURST_IMPULSE = "deu_particle_builder_burst_impluse";

    GeyserBaseParticleDialog(Collection<FramePoint> framePoints) {
        super(Component.text("Geyser Base / Geyser Poof Animation Particle"), List.of(
                DialogInput
                        .singleOption(GEYSER_PARTICLE,
                                Component.text("Particle"),
                                List.of(SingleOptionDialogInput.OptionEntry
                                                .create("geyser_base", Component.text("Geyser Base"), true),
                                        SingleOptionDialogInput.OptionEntry
                                                .create("geyser_poof", Component.text("Geyser Poof"), false)))
                        .build(),
                DialogInput
                        .numberRange(WATER_BLOCKS, Component.text("Water Blocks"), 0.0f, 4.0f)
                        .step(1.0f)
                        .initial(0.0f)
                        .build(),
                DialogInput
                        .numberRange(BURST_IMPULSE, Component.text("Burst Impulse"), 0.0f, 20.0f)
                        .step(0.1f)
                        .initial(0.0f)
                        .build()),
                framePoints);
    }

    @Override
    ParticleDialog create(Collection<FramePoint> framePoints) {
        return new GeyserBaseParticleDialog(framePoints);
    }

    @Override
    protected DialogActionCallback buildConfirmCallback(Collection<FramePoint> framePoints) {
        return (view, audience) -> {
            String particleStr = view.getText(GEYSER_PARTICLE);
            Particle particle = particleStr.equals("geyser_base") ? Particle.GEYSER_BASE : Particle.GEYSER_POOF;
            int waterBlocks = view.getFloat(WATER_BLOCKS).intValue();
            float burstImpulse = view.getFloat(BURST_IMPULSE);

            this.buildParticle(view, audience, particle, new Particle.GeyserBase(waterBlocks, burstImpulse), framePoints);
        };
    }
}
