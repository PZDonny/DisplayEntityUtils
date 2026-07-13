package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsAdaptTagsCMD extends PartsSubCommand {
    PartsAdaptTagsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("adapttags", parentSubCommand, Permission.PARTS_TAG, true);
        addFlag("-remove");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        if (group instanceof PacketDisplayEntityGroup){
            DisplayEntityPluginCommand.disallowPacketGroup(player);
            return false;
        }

        boolean removeFromSB = isRemoveFromScoreboard(player, args);

        for (SpawnedDisplayEntityPart part : ((SpawnedPartSelection) selection).getSelectedParts()){
            part.adaptScoreboardTags(removeFromSB);
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Adapted all scoreboard tags for parts in your selection!", NamedTextColor.GREEN)));
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (PartsCMD.isUnwantedPacketPart(player, selectedPart)) return false;

        boolean removeFromSB = isRemoveFromScoreboard(player, args);

        ((SpawnedDisplayEntityPart) selectedPart).adaptScoreboardTags(removeFromSB);

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Adapted all scoreboard tags for your selected part!", NamedTextColor.GREEN)));
        return false;
    }

    private boolean isRemoveFromScoreboard(Player player, String[] args){
        OptionalArguments oArgs = getOptionalArguments(player, args);
        return oArgs.hasFlag("-remove");
    }

    @Override
    protected String getDescription() {
        return "Adapt scoreboard tags to tags usable by DisplayEntityUtils. Applies to selected parts."+
                " \"-remove\" removes tag from scoreboard";
    }
}
