package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.util.List;

class DustTransitionParticleDialog extends ParticleDialog {

    private static final String START_COLOR = "deu_particle_builder_dust_color_1";
    private static final String END_COLOR = "deu_particle_builder_dust_color_2";
    private static final String SIZE = "deu_particle_builder_dust_size";

    DustTransitionParticleDialog() {
        super(Component.text("Dust Transition Animation Particle"), List.of(
                DialogInput
                        .text(START_COLOR, Component.text("Start Color (Minecraft Color or Hex)"))
                        .build(),
                DialogInput
                        .text(END_COLOR, Component.text("End Color (Minecraft Color or Hex)"))
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

            String color1String = view.getText(START_COLOR);
            Color color1 = ConversionUtils.getColorFromText(color1String);
            if (color1 == null){
                audience.sendMessage(Component.text("Failed to read the given start color: "+color1String, NamedTextColor.RED));
                return;
            }

            String color2String = view.getText(END_COLOR);
            Color color2 = ConversionUtils.getColorFromText(color2String);
            if (color2 == null){
                audience.sendMessage(Component.text("Failed to read the given end color: "+color2String, NamedTextColor.RED));
                return;
            }
            this.buildParticle(view, audience, Particle.DUST_COLOR_TRANSITION, new Particle.DustTransition(color1, color2, size));
        };
    }
}
