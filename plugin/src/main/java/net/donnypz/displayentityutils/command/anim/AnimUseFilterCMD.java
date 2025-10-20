package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.group.GroupCMD;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimUseFilterCMD extends PlayerSubCommand {
    AnimUseFilterCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("usefilter", parentSubCommand, Permission.ANIM_USE_FILTER);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (DisplayGroupManager.getSelectedGroup(player) == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedSingleSelection(player, selection)){
            return;
        }

        boolean trim = args.length > 0 && args[2].equalsIgnoreCase("-trim");
        anim.setFilter((SpawnedPartSelection) selection, trim);

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected animation will use your part section's filter", NamedTextColor.GREEN)));
        if (trim){
            player.sendMessage(Component.text("Trimmed redundant data (IRREVERSIBLE)", NamedTextColor.GRAY));
        }
    }
}