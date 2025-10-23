package net.donnypz.displayentityutils.utils.dialogs.animationparticles;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.donnypz.displayentityutils.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class AnimationParticleSelectDialog {

    private static final ClickCallback.Options CALLBACK_OPTIONS = ClickCallback.Options.builder().uses(ClickCallback.UNLIMITED_USES).build();

    /**
     * Send this dialog to a player
     * @param player the player
     */
    @ApiStatus.Internal
    public static void sendDialog(@NotNull Player player){
        Dialog dialog = Dialog.create(builder -> {
            builder.empty()
                    .base(DialogBase.builder(Component.text("Select an Animation Particle"))
                            //2 lines below required to stop flicker and centering of mouse
                            .pause(false)
                            .afterAction(DialogBase.DialogAfterAction.NONE)
                            .build())
                    .type(DialogType.multiAction(getActionButtons())
                            .build());
        });
        player.showDialog(dialog);
    }


    private static List<ActionButton> getActionButtons(){
        List<ActionButton> buttons = new ArrayList<>();
        buttons.add(getBlockParticleAction());
        buttons.add(getItemstackAction());
        buttons.add(getDustOptionAction());
        buttons.add(getDustTransitionAction());

        //V_1_21_5 Particles
        //Not checked for v1_20_5 since dialogs aren't even viewable on versions that low
        buttons.add(getEntityEffectAction());
        buttons.add(getTintedLeavesAction());

        if (VersionUtils.IS_1_21_9) buttons.add(getFlashAction());

        buttons.add(getGeneralAction());
        return buttons;
    }

    private static ActionButton getBlockParticleAction(){
        return ActionButton
                .builder(Component.text("Block Particle"))
                .tooltip(Component.text("Create an Animation Particle from a block's texture", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    AnimationParticleDialogs.BLOCK.sendDialog((Player) audience);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getItemstackAction(){
        return ActionButton
                .builder(Component.text("Item Particle"))
                .tooltip(Component.text("Create an Animation Particle from an item's texture", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    AnimationParticleDialogs.ITEM.sendDialog((Player) audience);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getDustOptionAction(){
        return ActionButton
                .builder(Component.text("Dust"))
                .tooltip(Component.text("Create a Dust Option Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    AnimationParticleDialogs.DUST_OPTION.sendDialog((Player) audience);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getDustTransitionAction(){
        return ActionButton
                .builder(Component.text("Dust Transition"))
                .tooltip(Component.text("Create a Dust Transition Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    AnimationParticleDialogs.DUST_TRANSITION.sendDialog((Player) audience);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getEntityEffectAction(){
        return ActionButton
                .builder(Component.text("Entity Effect"))
                .tooltip(Component.text("Create an Entity Effect Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    AnimationParticleDialogs.ENTITY_EFFECT.sendDialog((Player) audience);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getFlashAction(){
        return ActionButton
                .builder(Component.text("Flash"))
                .tooltip(Component.text("Create a Flash Animation Particle", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    AnimationParticleDialogs.FLASH.sendDialog((Player) audience);
                }, CALLBACK_OPTIONS))
                .build();
    }

    private static ActionButton getGeneralAction(){
        return ActionButton
                .builder(Component.text("General Particle"))
                .tooltip(Component.text("Create an Animation Particle from a basic particle without additional data", NamedTextColor.YELLOW))
                .action(DialogAction.customClick((view, audience) -> {
                    AnimationParticleDialogs.GENERAL.sendDialog((Player) audience);
                }, CALLBACK_OPTIONS))
                .build();
    }
}
