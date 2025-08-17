package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.util.List;

class DustOptionParticleDialog extends ParticleDialog {

    private static final String COLOR = "deu_particle_builder_dust_color";
    private static final String SIZE = "deu_particle_builder_dust_size";

    DustOptionParticleDialog() {
        super(Component.text("Dust Option Animation Particle"), List.of(
                DialogInput
                        .text(COLOR, Component.text("Color (Minecraft Color or Hex)"))
                        .build(),
                DialogInput
                        .numberRange(SIZE, Component.text("Size"), 0.0f, 100.0f)
                        .step(0.5f)
                        .initial(0.0f)
                        .build()));
    }

    @Override
    protected DialogActionCallback buildConfirmCallback() {
        return (view, audience) -> {
            float size = view.getFloat(SIZE);
            String colorString = view.getText(COLOR);
            Color color = ConversionUtils.getColorFromText(colorString);
            if (color == null){
                audience.sendMessage(Component.text("Failed to read the given color: "+colorString, NamedTextColor.RED));
                return;
            }
            this.buildParticle(view, audience, Particle.DUST, new Particle.DustOptions(color, size));
        };
    }
}
