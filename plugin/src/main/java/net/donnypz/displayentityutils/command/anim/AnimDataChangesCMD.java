package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimDataChangesCMD extends PlayerSubCommand {
    AnimDataChangesCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggledatachanges", parentSubCommand, Permission.ANIM_TOGGLE_DATA_CHANGES);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        boolean dataChanges = !anim.allowsTextureChanges();
        anim.allowTextureChanges(dataChanges);
        player.sendMessage(Component.text("Animation Data Changes toggled to: ", NamedTextColor.GREEN).append(Component.text(dataChanges, NamedTextColor.YELLOW)));
        player.sendMessage(Component.text("| Data Changes Apply to:", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- Block Display block changes", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- Item Display item changes", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- Text Display text changes", NamedTextColor.GRAY));
    }
}
