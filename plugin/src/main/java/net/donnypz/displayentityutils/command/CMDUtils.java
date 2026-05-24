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

    public static void sendCMD(CommandSender sender, String command){
        sendCMD(sender, command, null);
    }

    public static void sendCMD(CommandSender sender, String command, String description){
        Component msg = Component.text(command, TextColor.color(230, 230, 230));
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(Component.text(description, NamedTextColor.AQUA)));
        }
        sender.sendMessage(msg);
    }

    public static void sendUnsafeCMD(CommandSender sender, String command, String description){
        Component msg = UNSAFE.append(Component.text(command, TextColor.color(230, 230, 230)));
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(Component.text(description, NamedTextColor.AQUA)));
        }
        sender.sendMessage(msg);
    }

    public static void tryAddEntityToGroup(Player player, Entity entity, String[] args, int groupArg){
        String entityTypeName = entity.getType().getKey().getKey();
        if (args.length >= groupArg+1 && args[groupArg].equalsIgnoreCase("-g")){
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
