package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class CMDUtils {

    private static final Component UNSAFE = Component.text("[UNSAFE] ", NamedTextColor.RED);
    private static final TextColor COMMAND_COLOR = TextColor.color(230, 230, 230);

    public static void sendCMD(CommandSender sender, DEUSubCommand deuSubCommand){
        Component msg = Component.text(deuSubCommand.getShortCommandUsage(), COMMAND_COLOR);
        String description = deuSubCommand.getDescription();
        msg = msg.hoverEvent(HoverEvent.showText(
                Component.text(deuSubCommand.getCommandUsage(), NamedTextColor.YELLOW)
                        .appendNewline()
                        .append(Component.text(description, NamedTextColor.AQUA))));
        if (deuSubCommand.isUnsafe()) msg = UNSAFE.append(msg);
        sender.sendMessage(msg);
    }

    public static void sendCMD(CommandSender sender, String command, String description){
        Component msg = Component.text(command, COMMAND_COLOR);
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(Component.text(description, NamedTextColor.AQUA)));
        }
        sender.sendMessage(msg);
    }

    public static void sendCMD(CommandSender sender, String command, String description, String extraInfo){
        Component msg = Component.text(command, COMMAND_COLOR);
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(
                    Component.text(description, NamedTextColor.AQUA)
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text(extraInfo, NamedTextColor.YELLOW))));
        }
        sender.sendMessage(msg);
    }

    public static void tryAddEntityToGroup(Player player, String[] args, Entity entity, DEUSubCommand subCommand, String flag){
        String entityTypeName = entity.getType().getKey().getKey();
        if (subCommand.getOptionalArguments(player, args).hasFlag(flag)){
            ActiveGroup<?> group = DEUUser.getOrCreateUser(player).getSelectedGroup();
            if (group == null) {
                player.sendMessage(Component.text("- You must have a group selected to add the "+entityTypeName+" to a group", NamedTextColor.YELLOW));
                return;
            }

            ActivePart part = group.addEntity(entity);
            if (part == null){
                player.sendMessage(Component.text("- Failed to add the "+entityTypeName+" to your selected group", NamedTextColor.YELLOW));
                return;
            }
            ((MultiPartSelection<?>) DisplayGroupManager.getPartSelection(player)).refresh();

            player.sendMessage(Component.text("- The "+entityTypeName+" has been added to your selected group", NamedTextColor.GRAY));
        }
    }
}
