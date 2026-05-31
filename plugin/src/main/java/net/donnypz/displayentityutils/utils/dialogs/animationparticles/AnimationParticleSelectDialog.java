package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnimationParticleSelectDialog {

    private static final ClickCallback.Options CALLBACK_OPTIONS = ClickCallback.Options.builder()
            .lifetime(Duration.ofMinutes(15))
            .uses(ClickCallback.UNLIMITED_USES)
            .build();

    public static void sendDialog(@NotNull Player player){
        Dialog dialog = Dialog.create(builder -> {
            builder.empty()
                    .base(DialogBase.builder(Component.text("Select an Animation Particle"))
                            //2 lines below required to stop flicker and centering of mouse
                            .pause(false)
                            .afterAction(DialogBase.DialogAfterAction.NONE)
                            .build())
                    .type(DialogType.multiAction(getActionButtons(null))
                            .build());
        });
        player.showDialog(dialog);
    }

    public static void sendDialog(@NotNull Player player, Collection<FramePoint> framePoints){
        Dialog dialog = Dialog.create(builder -> {
            builder.empty()
                    .base(DialogBase.builder(Component.text("Select an Animation Particle"))
                            //2 lines below required to stop flicker and centering of mouse
                            .pause(false)
                            .afterAction(DialogBase.DialogAfterAction.NONE)
                            .build())
                    .type(DialogType.multiAction(getActionButtons(framePoints))
                            .build());
        });
        player.showDialog(dialog);
    }


    private static List<ActionButton> getActionButtons(Collection<FramePoint> framePoints){
        List<ActionButton> buttons = new ArrayList<>();
        buttons.add(getBlockParticleAction(framePoints));
        buttons.add(getItemstackAction(framePoints));
        buttons.add(getDustOptionAction(framePoints));
        buttons.add(getDustTransitionAction(framePoints));

        //V_1_21_5 Particles
        //Not checked for v1_20_5 since dialogs aren't even viewable on versions that low
        buttons.add(getEntityEffectAction(framePoints));
        buttons.add(getTintedLeavesAction(framePoints));

        if (VersionUtils.IS_1_21_9) buttons.add(getFlashAction(framePoints));

        buttons.add(getGeneralAction(framePoints));
        return buttons;
    }

    private static ActionButton getBlockParticleAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("Block Particle"))
                .tooltip(Component.text("Create an Animation Particle from a block's texture", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.BLOCK);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getItemstackAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("Item Particle"))
                .tooltip(Component.text("Create an Animation Particle from an item's texture", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.ITEM);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getDustOptionAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("Dust"))
                .tooltip(Component.text("Create a Dust Option Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.DUST_OPTION);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getDustTransitionAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("Dust Transition"))
                .tooltip(Component.text("Create a Dust Transition Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.DUST_TRANSITION);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getEntityEffectAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("Entity Effect"))
                .tooltip(Component.text("Create an Entity Effect Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.ENTITY_EFFECT);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getTintedLeavesAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("Tinted Leaves"))
                .tooltip(Component.text("Create a Tinted Leaves Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.TINTED_LEAVES);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getFlashAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("Flash"))
                .tooltip(Component.text("Create a Flash Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.FLASH);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getGeneralAction(final Collection<FramePoint> framePoints){
        return ActionButton
                .builder(Component.text("General Particle"))
                .tooltip(Component.text("Create an Animation Particle from a basic particle without additional data", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    createOrSendExisting(framePoints, (Player) audience, AnimationParticleDialogs.GENERAL);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static void createOrSendExisting(Collection<FramePoint> framePoints, Player player, ParticleDialog dialog){
        if (framePoints == null){
            dialog.sendDialog(player);
        }
        else{
            dialog.create(framePoints).sendDialog(player);
        }
    }
}
