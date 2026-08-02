package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.GroupSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.Axis;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GroupSetRotationCMD extends GroupSubCommand {

    private static final String DISPLAYS_ONLY_FLAG = "-displaysonly";

    public GroupSetRotationCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("setrotation", parentSubCommand, Permission.GROUP_TRANSFORM, true);
        setTabComplete(2, List.of("x", "y", "z"));
        setTabComplete(3, "<angle-in-degrees>");
        addFlag(DISPLAYS_ONLY_FLAG);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }
        try{
            String axisInput = args[2];
            Axis axis = Axis.valueOf(axisInput.toUpperCase());
            float rotation = Float.parseFloat(args[3]);

            OptionalArguments oArgs = getOptionalArguments(player, args);
            boolean displaysOnly = oArgs.hasFlag(DISPLAYS_ONLY_FLAG);

            group.setGroupRotation(rotation, axis, !displaysOnly);

            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage
                    .miniMessage()
                    .deserialize(String.format("<green>Set your selected group's local <yellow>%s <green>axis rotation to <yellow>%s <green>degrees!",
                            axis.name(),
                            rotation))));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
        }
        catch(IllegalArgumentException e){
            super.incorrectUsage(player);
        }
    }

    @Override
    protected String getDescription() {
        return "Rotate a group around a given local space axis. " +
                "Use \""+DISPLAYS_ONLY_FLAG+"\" to only rotate display entities.";
    }
}
