package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class AnimDataChangesCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_TOGGLE_DATA_CHANGES)){
            return;
        }


        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        boolean dataChanges = !anim.allowsDataChanges();
        anim.allowDataChanges(dataChanges);
        player.sendMessage(Component.text("Animation Data Changes toggled to: ", NamedTextColor.GREEN).append(Component.text(dataChanges, NamedTextColor.YELLOW)));
        player.sendMessage(Component.text("| Data Changes Apply to:", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- Block Display block changes", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- Item Display item changes", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- Text Display text changes", NamedTextColor.GRAY));
    }
}
