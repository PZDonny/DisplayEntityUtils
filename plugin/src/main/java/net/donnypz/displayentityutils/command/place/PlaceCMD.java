package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceCMD extends ConsoleUsableSubCommand {
    public PlaceCMD() {
        super(Permission.HELP, new PlaceHelpCMD());
        new PlaceSetCMD(this);
        new PlaceUnsetCMD(this);
        new PlaceSetPermissionCMD(this);
        new PlaceUnsetPermissionCMD(this);
        new PlaceTogglePacketCMD(this);
        new PlaceTogglePlayerFacingCMD(this);
        new PlaceToggleBlockFaceCMD(this);
        new PlaceInfoCMD(this);
        new PlaceAddSoundCMD(this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            help(sender, 1);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            help(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void help(CommandSender sender, int page){
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        if (page == 1){
            CMDUtils.sendCMD(sender, "/deu place help", "Get help for placeable groups");
            CMDUtils.sendCMD(sender, "/deu place info", "Get placeable group information for your held block");
            CMDUtils.sendCMD(sender, "/deu place set <group-tag>", "Assign a group to your held block, which will be spawned when the block is placed");
            CMDUtils.sendCMD(sender, "/deu place unset", "Unassign a group from your held block");
            CMDUtils.sendCMD(sender, "/deu place setpermission", "Set the permission required to place the group");
            CMDUtils.sendCMD(sender, "/deu place unsetpermission", "Set the permission required to place the group");
            CMDUtils.sendCMD(sender, "/deu place togglepacket", "Toggle whether the placed group will be packet-based. True by default");
        }
        else{
            CMDUtils.sendCMD(sender, "/deu place toggleplayerfacing", "Toggle whether the placed group will respect the player's facing direction. True by default");
            CMDUtils.sendCMD(sender, "/deu place toggleblockface", "Toggle whether the placed group will respect the block face it is placed on. True by default");
            CMDUtils.sendCMD(sender, "/deu place addsound <place | break> <sound> <volume> <pitch>", "Add a sound to play when the block is placed or broken");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }

    static ItemStack getHeldItem(Player player, boolean mustBeAssigned){
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.getType().isBlock() || heldItem.getType() == Material.AIR){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must be holding a block to do this command!", NamedTextColor.RED)));
            return null;
        }

        if (mustBeAssigned && !PlaceableGroupManager.hasData(heldItem)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item does not have placeable group data!", NamedTextColor.RED)));
            return null;
        }
        return heldItem;
    }
}
