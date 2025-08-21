package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

class TextSetCMD extends PlayerSubCommand {

    public TextSetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("set", parentSubCommand, Permission.TEXT_SET_TEXT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis text set <text>", NamedTextColor.RED));
            return;
        }

        ServerSideSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
        }

        SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }


        TextDisplay display = (TextDisplay) selected.getEntity();
        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < args.length; i++){
            builder.append(args[i]);
            if (i+1 != args.length){
                builder.append(" ");
            }
        }
        String textResult = builder.toString().replace("\\n", "\n");
        Key oldFont = display.text().font();
        Component comp = LegacyComponentSerializer.legacyAmpersand().deserialize(textResult);
        display.text(comp.font(oldFont));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set text on text display!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("Keep in mind, you can include \"\\n\" in your text display to create a new line.", NamedTextColor.GRAY));
    }
}
