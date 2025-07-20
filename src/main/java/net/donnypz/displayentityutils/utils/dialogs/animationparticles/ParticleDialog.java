package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.command.FramePointDisplay;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ParticleDialog {


    private static final String COUNT = "deu_particle_builder_count";
    private static final String EXTRA = "deu_particle_builder_extra";
    private static final String X_OFFSET = "deu_particle_builder_x_offset";
    private static final String Y_OFFSET = "deu_particle_builder_y_offset";
    private static final String Z_OFFSET = "deu_particle_builder_z_offset";
    private static final ClickCallback.Options CALLBACK_OPTIONS = ClickCallback.Options.builder().uses(ClickCallback.UNLIMITED_USES).build();

    private final Dialog dialog;
    private final ActionButton cancelButton = ActionButton
            .builder(Component.text("Cancel", NamedTextColor.RED))
            .tooltip(Component.text("Cancel animation particle creation"))
            .action(DialogAction.customClick((view, audience) -> {
                Player p = (Player) audience;
                p.sendMessage(DisplayEntityPlugin.pluginPrefix
                        .append(Component.text("Animation Particle creation cancelled!", NamedTextColor.RED)));
            }, CALLBACK_OPTIONS))
            .build();

    ParticleDialog(Component dialogTitle, @Nullable List<DialogInput> additionalInputs){
        this.dialog = Dialog.create(builder -> {
            builder.empty()
                    .type(buildDialogType(buildConfirmCallback()))
                    .base(DialogBase.builder(dialogTitle)
                            .inputs(getInputs(additionalInputs))
                            .build());
        });
    }

    void sendDialog(Player player){
        player.showDialog(dialog);
    }

    protected void buildParticle(DialogResponseView view, Audience audience, Particle particle, Object data){
        int count = Math.round(view.getFloat(COUNT));
        double extra = (double) view.getFloat(EXTRA);
        double xOffset = (double) view.getFloat(X_OFFSET);
        double yOffset = (double) view.getFloat(Y_OFFSET);
        double zOffset = (double) view.getFloat(Z_OFFSET);
        Player p = (Player) audience;
        FramePointDisplay display = (FramePointDisplay) DEUCommandUtils.getSelectedRelativePoint(p);
        FramePoint framePoint = display.getRelativePoint();
        AnimationParticleBuilder builder = AnimationParticleBuilder.create(framePoint, particle, count, xOffset, yOffset, zOffset, extra, data);
        builder.build();
        builder.remove();
        p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Animation Particle creation successful!", NamedTextColor.GREEN)));
    }

    protected abstract DialogActionCallback buildConfirmCallback();


    private List<DialogInput> getInputs(@Nullable List<DialogInput> additionalInputs){
        List<DialogInput> inputs = new ArrayList<>();
        if (additionalInputs != null) inputs.addAll(additionalInputs);
        inputs.add(DialogInput
                .numberRange(COUNT, Component.text("Count"), 1.0f, 500.0f)
                .step(1.0f)
                .initial(1.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(EXTRA, Component.text("Extra"), 0.0f, 100.0f)
                .step(1.0f)
                .initial(0.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(X_OFFSET, Component.text("Delta X (Offset)"), 0.0f, 100.0f)
                .step(0.5f)
                .initial(0.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(Y_OFFSET, Component.text("Delta Y (Offset)"), 0.0f, 100.0f)
                .step(0.5f)
                .initial(0.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(Z_OFFSET, Component.text("Delta Z (Offset)"), 0.0f, 100.0f)
                .step(0.5f)
                .initial(0.0f)
                .build());
        return inputs;
    }

    private DialogType buildDialogType(DialogActionCallback callback){
        return DialogType.confirmation(
                ActionButton
                        .builder(Component.text("Build", NamedTextColor.GREEN))
                        .tooltip(Component.text("Build and add this particle to your selected frame point"))
                        .action(DialogAction.customClick(callback, CALLBACK_OPTIONS))
                        .build(),
                cancelButton);
    }
}