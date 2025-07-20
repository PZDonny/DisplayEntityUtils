package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsBillboardCMD extends PlayerSubCommand {
    PartsBillboardCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("billboard", parentSubCommand, Permission.PARTS_BILLBOARD);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis parts billboard <fixed | vertical | horizontal | center> [-all]", NamedTextColor.GRAY));
            return;
        }


        try{
            Display.Billboard billboard = Display.Billboard.valueOf(args[2].toUpperCase());
            if (args.length >= 4 && args[3].equalsIgnoreCase("-all")) {
                selection.setBillboard(billboard);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Billboard successfully set for selected display entity part(s) in your selection!", NamedTextColor.GREEN)));
            }
            else{
                SpawnedDisplayEntityPart selectedPart = selection.getSelectedPart();
                if (selectedPart.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Interaction entities cannot have a billboard applied!", NamedTextColor.RED)));
                }
                else{
                    selectedPart.setBillboard(billboard);
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Billboard successfully set for your selected part!", NamedTextColor.GREEN)));
                }
            }
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid billboard type!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis parts billboard <fixed | vertical | horizontal | center> [-all]", NamedTextColor.GRAY));
        }


    }
}
