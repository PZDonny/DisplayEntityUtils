package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PartsCloneCMD extends PartsSubCommand {

    private final String ADD_TAG = "-addtag";
    public PartsCloneCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("clone", parentSubCommand, Permission.PARTS_CLONE, true);
        addOption(ADD_TAG, "<part-tag-to-add>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (!oArgs.isValidOptions()) return false;

        String tag = oArgs.getOption(ADD_TAG);
        if (!DisplayUtils.isValidTag(tag) || tag.isBlank()) {
            DisplayEntityPluginCommand.invalidTag(player, tag);
            return false;
        }

        for (ActivePart part : selection.getParts()){
            ActivePart clonedPart = part.clone();
            if (clonedPart == null) continue;

            if (!tag.isBlank()){
                clonedPart.addTag(tag);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Selected parts cloned!", NamedTextColor.GREEN)));
        if (!tag.isBlank()) player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>| <yellow>Tag added to cloned parts <white>("+tag+")"));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        ActivePart clonedPart = selectedPart.clone();
        if (clonedPart == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to clone part!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Invalid selection or your group's parent/master part is selected", NamedTextColor.GRAY));
            return false;
        }

        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (!oArgs.isValidOptions()) return false;

        String tag = oArgs.getOption(ADD_TAG);
        if (!DisplayUtils.isValidTag(tag) || tag.isBlank()) {
            DisplayEntityPluginCommand.invalidTag(player, tag);
            return false;
        }
        else{
            clonedPart.addTag(tag);
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Part cloned!", NamedTextColor.GREEN)));
        if (selection instanceof SinglePartSelection){
            SinglePartSelection newSelection = new SinglePartSelection((SpawnedDisplayEntityPart) clonedPart);

            DEUUser user = DEUUser.getUser(player);
            user.setSelectedPartSelection(newSelection, false);
            player.sendMessage(Component.text("| Your cloned part has been automatically selected.", NamedTextColor.GRAY));
        }
        else if (selection instanceof MultiPartSelection<?> mp){
            mp.refresh();
            player.sendMessage(Component.text("| Your cloned part has been automatically selected and added to your group", NamedTextColor.GRAY));
        }
        if (!tag.isBlank()) player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>| <yellow>Tag added <white>("+tag+")"));
        return true;
    }

    @Override
    protected String getDescription() {
        return "Create a clone of your selected part(s). Use \""+ADD_TAG+"\" to optionally add a part tag to the cloned part(s)";
    }
}
