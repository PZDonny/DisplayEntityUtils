package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.GroupSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

public class GroupRotateCMD extends GroupSubCommand {

    private static final String DISPLAYS_ONLY_FLAG = "-displaysonly";

    public GroupRotateCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("rotate", parentSubCommand, Permission.GROUP_TRANSFORM, true);
        setTabComplete(2, List.of("x", "y", "z"));
        setTabComplete(3, "<angle-in-degrees>");
        addFlag("-world");
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
            String axis = args[2];
            if (!(axis.equalsIgnoreCase("x") || axis.equalsIgnoreCase("y") || axis.equalsIgnoreCase("z"))){
                super.incorrectUsage(player);
                return;
            }

            float rotation = Float.parseFloat(args[3]);
            if (rotation == 0) {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a non-zero number for the rotation, in degrees!", NamedTextColor.RED)));
                return;
            }

            Quaternionf q = new Quaternionf();
            float rotRad = (float) Math.toRadians(rotation);
            if (axis.equalsIgnoreCase("x")){
                q.rotateX(rotRad);
            }
            else if (axis.equalsIgnoreCase("y")){
                q.rotateY(rotRad);
            }
            else{
                q.rotateZ(rotRad);
            }
            OptionalArguments oArgs = getOptionalArguments(player, args);
            boolean worldSpace = oArgs.hasFlag("-world");
            boolean displaysOnly = oArgs.hasFlag(DISPLAYS_ONLY_FLAG);
            if (!displaysOnly){
                group.pivotAndRotate(q, group.getLocation(), worldSpace);
            }
            else{
                group.rotate(q, worldSpace);
            }

            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage
                    .miniMessage()
                    .deserialize(String.format("<green>Rotating your selected group on the %s <yellow>%s <green>axis by <yellow>%s <green>degrees!",
                            worldSpace ? "world" : "local",
                            axis.toUpperCase(),
                            rotation))));
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            }
        }
    }

    @Override
    protected String getDescription() {
        return "Rotate a group around a given axis. " +
                "Use \"-world\" to rotate in world space. " +
                "Use \""+DISPLAYS_ONLY_FLAG+"\" to only rotate display entities.";
    }
}
