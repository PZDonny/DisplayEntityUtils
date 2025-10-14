package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsResetFilterCMD extends PlayerSubCommand {
    PartsResetFilterCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("resetfilter", parentSubCommand, Permission.PARTS_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            PartsCMD.noPartSelection(player);
            return;
        }


        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        ((MultiPartSelection<?>) sel).reset(true);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Part Selection Filter Reset!", NamedTextColor.YELLOW)));
    }
}
