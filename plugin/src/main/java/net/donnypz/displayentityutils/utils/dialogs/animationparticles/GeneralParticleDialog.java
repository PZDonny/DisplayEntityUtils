package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Registry;

import java.util.Collection;
import java.util.List;

class GeneralParticleDialog extends ParticleDialog {

    private static final String PARTICLE = "deu_particle_builder_particle";

    GeneralParticleDialog(Collection<FramePoint> framePoints) {
        super(Component.text("General Animation Particle"),
                List.of(DialogInput.text(PARTICLE, Component.text("Particle")).build()),
                framePoints);
    }

    @Override
    ParticleDialog create(Collection<FramePoint> framePoints) {
        return new GeneralParticleDialog(framePoints);
    }

    @Override
    protected DialogActionCallback buildConfirmCallback(Collection<FramePoint> framePoints) {
        return (view, audience) -> {
            String particleId = view.getText(PARTICLE);
            Particle particle = Registry.PARTICLE_TYPE.get(Key.key("minecraft", particleId));
            if (particle == null){
                audience.sendMessage(Component.text("Failed to find particle: "+particleId, NamedTextColor.RED));
                return;
            }
            this.buildParticle(view, audience, particle, null, framePoints);
        };
    }
}
