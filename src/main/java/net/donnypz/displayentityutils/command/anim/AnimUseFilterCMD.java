package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
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
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        ServerSideSelection selection = DisplayGroupManager.getPartSelection(player);
        if (PartsCMD.isUnwantedSingleSelection(player, selection)){
            return;
        }
        if (selection == null){
            PartsCMD.noPartSelection(player);
            return;
        }
        boolean trim = args.length > 0 && args[0].equalsIgnoreCase("-trim");
        anim.setFilter((SpawnedPartSelection) selection, trim);

        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your selected animation will use your part section's filter", NamedTextColor.GREEN)));
        if (trim){
            player.sendMessage(Component.text("Trimmed redundant data (IRREVERSIBLE)", NamedTextColor.GRAY));
        }
    }
}