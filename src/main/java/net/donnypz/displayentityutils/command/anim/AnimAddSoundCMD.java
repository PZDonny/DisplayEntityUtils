package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.AnimationSound;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.command.FramePointDisplay;
import net.donnypz.displayentityutils.utils.command.RelativePointDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimAddSoundCMD extends PlayerSubCommand {
    AnimAddSoundCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addsound", parentSubCommand, Permission.ANIM_ADD_SOUND);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        RelativePointDisplay rp = DEUCommandUtils.getSelectedRelativePoint(player);
        if (!(rp instanceof FramePointDisplay display)){
            AnimCMD.noFramePointSelection(player);
            return;
        }

        if (args.length < 6) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim addsound <sound> <volume> <pitch> <delay-in-ticks>", NamedTextColor.RED));
            return;
        }
        try {
            String soundString = args[2].replace(".", "_").toUpperCase();
            Sound sound = Registry.SOUNDS.get(Key.key("minecraft", soundString));
            if (sound == null){
                player.sendMessage(Component.text("Invalid Sound Name!", NamedTextColor.RED));
                return;
            }
            float volume = Float.parseFloat(args[3]);
            float pitch = Float.parseFloat(args[4]);
            int delayInTicks = Integer.parseInt(args[5]);

            display.getRelativePoint().addSound(new AnimationSound(sound, volume, pitch, delayInTicks));

            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Sound Added to Frame Point!", NamedTextColor.GREEN)));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| Sound: <yellow>"+sound));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| Volume: <yellow>"+volume));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| Pitch: <yellow>"+pitch));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| Delay: <yellow>"+delayInTicks));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid number entered! Enter a number >= 0", NamedTextColor.RED));
            player.sendMessage(Component.text("| Delay must be a whole number", NamedTextColor.GRAY));
        }
    }
}
