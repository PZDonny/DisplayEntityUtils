package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DisplayBrightnessCMD extends PartsSubCommand {
    DisplayBrightnessCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("brightness", parentSubCommand, Permission.DISPLAY_BRIGHTNESS, 4, 4);
        setTabComplete(2, "<block>");
        setTabComplete(3, "<sky>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("/deu display brightness <block> <sky> [-all]", NamedTextColor.RED));
        player.sendMessage(Component.text("| Brightness can be whole numbers between 0 and 15", NamedTextColor.GRAY));
        player.sendMessage(Component.text("| Set both \"block\" and \"sky\" to -1 to reset brightness", NamedTextColor.GRAY));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        BrightnessResult result = buildBrightness(args[2], args[3]);
        if (!result.correctNumbers()){
            sendIncorrectUsage(player);
            return false;
        }
        Display.Brightness brightness = result.brightness;
        if (brightness == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness reset for displays in your selection!", NamedTextColor.YELLOW)));
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness set for displays in your selection!", NamedTextColor.GREEN)));
        }
        selection.setBrightness(brightness);
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isNotDisplay(player, selectedPart)) return false;
        BrightnessResult result = buildBrightness(args[2], args[3]);
        if (!result.correctNumbers()){
            sendIncorrectUsage(player);
            return false;
        }
        Display.Brightness brightness = result.brightness;
        if (brightness == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness reset for your selected display!", NamedTextColor.YELLOW)));
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness set for your selected display!", NamedTextColor.GREEN)));
        }
        selectedPart.setBrightness(brightness);
        return true;
    }

    private BrightnessResult buildBrightness(String arg1, String arg2){
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
            return new BrightnessResult(null, false);
        }
    }

    private record BrightnessResult(Display.Brightness brightness, boolean correctNumbers){}
}
