package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsBrightnessCMD extends PartsSubCommand {
    PartsBrightnessCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("brightness", parentSubCommand, Permission.PARTS_BRIGHTNESS, 4, 4);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("/mdis parts brightness <block> <sky> [-all]", NamedTextColor.RED));
        player.sendMessage(Component.text("| Brightness can be whole numbers between 0 and 15", NamedTextColor.GRAY));
        player.sendMessage(Component.text("| Set both \"block\" and \"sky\" to -1 to reset brightness", NamedTextColor.GRAY));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        BrightnessResult result = buildBrightness(player, args[2], args[3]);
        if (!result.correctNumbers()){
            sendIncorrectUsage(player);
            return;
        }
        Display.Brightness brightness = result.brightness;
        if (brightness == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness reset for part selection!", NamedTextColor.YELLOW)));
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness set for your part selection!", NamedTextColor.GREEN)));
        }
        selection.setBrightness(brightness);
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        BrightnessResult result = buildBrightness(player, args[2], args[3]);
        if (!result.correctNumbers()){
            sendIncorrectUsage(player);
            return;
        }
        Display.Brightness brightness = result.brightness;
        if (brightness == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness reset for your selected part!", NamedTextColor.YELLOW)));
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness set for your selected part!", NamedTextColor.GREEN)));
        }
        selectedPart.setBrightness(brightness);
    }

    private BrightnessResult buildBrightness(Player player, String arg1, String arg2){
        try{
            int block = Integer.parseInt(arg1);
            int sky = Integer.parseInt(arg2);
            if (sky > 15 || sky < 0 || block > 15 || block < 0){
                if (sky == -1 && block == -1){ //Reset Brightness
                    return new BrightnessResult(null, true);
                }
                else {
                    throw new IllegalArgumentException();
                }
            }
            else{
                return new BrightnessResult(new Display.Brightness(block, sky), true);
            }
        }
        catch(IllegalArgumentException e){
            sendIncorrectUsage(player);
            return new BrightnessResult(null, false);
        }
    }

    private record BrightnessResult(Display.Brightness brightness, boolean correctNumbers){}
}
