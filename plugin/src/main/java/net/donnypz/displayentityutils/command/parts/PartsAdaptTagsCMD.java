package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsAdaptTagsCMD extends PartsSubCommand {
    PartsAdaptTagsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("adapttags", parentSubCommand, Permission.PARTS_TAG, 2, 0);
        setTabComplete(2, "-remove");
    }

    @Override
    public void execute(Player player, String[] args) {}

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {}

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selection instanceof PacketPartSelection){
            DisplayEntityPluginCommand.disallowPacketGroup(player);
            return;
        }

        boolean removeFromSB;
        if (args.length < 3){
            removeFromSB = false;
        }
        else{
            removeFromSB = args[2].equalsIgnoreCase("-remove");
        }

        if (!selection.isValid()){ //Adapt for all parts
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid part selection! Please try again!", NamedTextColor.RED)));
            return;
        }
        if (PartsCMD.isUnwantedSingleSelection(player, selection)){
            return;
        }

        for (SpawnedDisplayEntityPart part : ((SpawnedPartSelection) selection).getSelectedParts()){
            part.adaptScoreboardTags(removeFromSB);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Adapted all scoreboard tags in your part selection!", NamedTextColor.GREEN)));
    }

}
