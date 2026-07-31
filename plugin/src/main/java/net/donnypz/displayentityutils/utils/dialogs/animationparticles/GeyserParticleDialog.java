package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;

import java.util.Collection;
import java.util.List;

class GeyserParticleDialog extends ParticleDialog {

    private static final String GEYSER_PARTICLE = "deu_particle_builder_geyser_particle";
    private static final String WATER_BLOCKS = "deu_particle_builder_water_blocks";

    GeyserParticleDialog(Collection<FramePoint> framePoints) {
        super(Component.text("Geyser / Geyser Plume Animation Particle"), List.of(
                        DialogInput
                                .singleOption(GEYSER_PARTICLE,
                                        Component.text("Particle"),
                                        List.of(SingleOptionDialogInput.OptionEntry
                                                        .create("geyser", Component.text("Geyser"), true),
                                                SingleOptionDialogInput.OptionEntry
                                                        .create("geyser_plume", Component.text("Geyser Plume"), false)))
                                .build(),
                        DialogInput
                                .numberRange(WATER_BLOCKS, Component.text("Water Blocks"), 0.0f, 4.0f)
                                .step(1.0f)
                                .initial(0.0f)
                                .build()),
                framePoints);
    }

    @Override
    ParticleDialog create(Collection<FramePoint> framePoints) {
        return new GeyserParticleDialog(framePoints);
    }

    @Override
    protected DialogActionCallback buildConfirmCallback(Collection<FramePoint> framePoints) {
        return (view, audience) -> {
            String particleStr = view.getText(GEYSER_PARTICLE);
            Particle particle = particleStr.equals("geyser") ? Particle.GEYSER : Particle.GEYSER_PLUME;
            int waterBlocks = view.getFloat(WATER_BLOCKS).intValue();

            this.buildParticle(view, audience, particle, new Particle.Geyser(waterBlocks), framePoints);
        };
    }
}
