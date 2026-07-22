package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.GroupSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

public class GroupRotateCMD extends GroupSubCommand {
    public GroupRotateCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("rotate", parentSubCommand, Permission.GROUP_TRANSFORM, true);
        setTabComplete(2, List.of("x", "y", "z"));
        setTabComplete(3, "<angle-in-degrees>");
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
            if (!(axis.equals("x") || axis.equals("y") || axis.equals("z"))){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid axis")));
                return;
            }

            float rotation = Float.parseFloat(args[3]);
            if (rotation == 0) {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a non-zero number for the rotation, in degrees!", NamedTextColor.RED)));
                return;
            }

            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Rotating your selected group!", NamedTextColor.GREEN)));
            Quaternionf q = new Quaternionf();
            float rotRad = (float) Math.toRadians(rotation);
            if (axis.equals("x")){
                q.rotateX(rotRad);
            }
            else if (axis.equals("y")){
                q.rotateY(rotRad);
            }
            else{
                q.rotateZ(rotRad);
            }
            group.rotateDisplays(q);
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            }
        }
    }

    @Override
    protected String getDescription() {
        return "Rotate a group around a given axis.";
    }
}
