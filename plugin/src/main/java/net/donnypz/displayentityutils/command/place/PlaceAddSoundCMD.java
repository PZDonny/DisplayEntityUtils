package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DEUSound;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PlaceAddSoundCMD extends PlayerSubCommand {
    PlaceAddSoundCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addsound", parentSubCommand, Permission.PLACE_SOUND);
        setTabComplete(2, List.of("place", "break"));
        setTabComplete(3, "<sound>");
        setTabComplete(4, "<volume>");
        setTabComplete(5, "<pitch>");
    }

    private void incorrectUsage(Player player){
        player.sendMessage(Component.text("Incorrect Usage! /deu anim addsound <place | break> <sound> <volume> <pitch>", NamedTextColor.RED));
    }

    @Override
    public void execute(Player player, String[] args) {

        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        if (args.length < 6) {
            incorrectUsage(player);
            return;
        }
        try {
            String isPlaceStr = args[2];
            boolean isPlace;
            if (isPlaceStr.equalsIgnoreCase("place")){
                isPlace = true;
            }
            else if (isPlaceStr.equalsIgnoreCase("break")){
                isPlace = false;
            }
            else{
                incorrectUsage(player);
                return;
            }

            String soundStr = args[3];
            float volume = Float.parseFloat(args[4]);
            float pitch = Float.parseFloat(args[5]);
            PlaceableGroupManager.addSound(heldItem, new DEUSound(soundStr, volume, pitch, 0), isPlace);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Sound Added! ", NamedTextColor.GREEN).append(Component.text(isPlace ? "(Place)" : "(Break)", NamedTextColor.GRAY))));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| Sound: <yellow>"+soundStr));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| Volume: <yellow>"+volume));
            player.sendMessage(MiniMessage.miniMessage().deserialize("| Pitch: <yellow>"+pitch));

            if (VersionUtils.getSound(soundStr) == null){
                player.sendMessage(Component.text("| The provided sound is not a vanilla Minecraft sound, or does not exist in this game version!", NamedTextColor.GRAY));
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid number entered! Enter numbers >= 0", NamedTextColor.RED));
        }
    }
}
