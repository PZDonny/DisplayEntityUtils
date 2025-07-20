package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.util.List;

class EntityEffectParticleDialog extends ParticleDialog {

    private static final String COLOR = "deu_particle_builder_entity_effect_color";

    EntityEffectParticleDialog() {
        super(Component.text("Dust Option Animation Particle"), List.of(
                DialogInput
                        .text(COLOR, Component.text("Color (Minecraft Color or Hex)"))
                        .build()));
    }

    @Override
    protected DialogActionCallback buildConfirmCallback() {
        return (view, audience) -> {
            String colorString = view.getText(COLOR);
            Color color = DEUCommandUtils.getColorFromText(colorString);
            if (color == null){
                audience.sendMessage(Component.text("Failed to read the given color: "+colorString, NamedTextColor.RED));
                return;
            }
            this.buildParticle(view, audience, Particle.ENTITY_EFFECT, color);
        };
    }
}
