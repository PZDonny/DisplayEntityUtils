package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.relativepoints.FramePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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
                p.sendMessage(DisplayAPI.pluginPrefix
                        .append(Component.text("Animation Particle creation cancelled!", NamedTextColor.RED)));
            }, CALLBACK_OPTIONS))
            .build();
    private final Component dialogTitle;

    ParticleDialog(Component dialogTitle, @Nullable List<DialogInput> additionalInputs, Collection<FramePoint> points){
        this.dialogTitle = dialogTitle;
        this.dialog = Dialog.create(builder -> {
            builder.empty()
                    .type(buildDialogType(buildConfirmCallback(points)))
                    .base(DialogBase.builder(this.dialogTitle)
                            .inputs(getInputs(additionalInputs))
                            .build());
        });
    }

    void sendDialog(Player player){
        player.showDialog(dialog);
    }

    abstract ParticleDialog create(Collection<FramePoint> framePoints);

    protected void buildParticle(DialogResponseView view, Audience audience, Particle particle, Object data, Collection<FramePoint> points){
        int count = Math.round(view.getFloat(COUNT));
        double extra = (double) view.getFloat(EXTRA);
        double xOffset = (double) view.getFloat(X_OFFSET);
        double yOffset = (double) view.getFloat(Y_OFFSET);
        double zOffset = (double) view.getFloat(Z_OFFSET);
        Player p = (Player) audience;
        AnimationParticleBuilder builder;
        if (points == null){
            FramePointSelector display = (FramePointSelector) RelativePointUtils.getRelativePointSelector(p);
            FramePoint framePoint = display.getRelativePoint();
            builder = AnimationParticleBuilder.create(framePoint, particle, count, xOffset, yOffset, zOffset, extra, data);
        }
        else{
            builder = AnimationParticleBuilder.create(points, particle, count, xOffset, yOffset, zOffset, extra, data);
        }

        try{
            builder.build();
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Animation Particle created for frame(s)!", NamedTextColor.GREEN)));
        }
        catch(RuntimeException ex){
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to create animation particle! See console.", NamedTextColor.RED)));
            ex.printStackTrace();
        }

        builder.remove();

    }

    protected abstract DialogActionCallback buildConfirmCallback(Collection<FramePoint> points);


    private List<DialogInput> getInputs(@Nullable List<DialogInput> additionalInputs){
        List<DialogInput> inputs = new ArrayList<>();
        if (additionalInputs != null) inputs.addAll(additionalInputs);
        inputs.add(DialogInput
                .numberRange(COUNT, Component.text("Count"), 1.0f, 500.0f)
                .step(1.0f)
                .initial(1.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(EXTRA, Component.text("Extra"), 0.0f, 15.0f)
                .step(0.1f)
                .initial(0.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(X_OFFSET, Component.text("Delta X (Offset)"), 0.0f, 15.0f)
                .step(0.1f)
                .initial(0.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(Y_OFFSET, Component.text("Delta Y (Offset)"), 0.0f, 15.0f)
                .step(0.1f)
                .initial(0.0f)
                .build());
        inputs.add(DialogInput
                .numberRange(Z_OFFSET, Component.text("Delta Z (Offset)"), 0.0f, 15.0f)
                .step(0.1f)
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