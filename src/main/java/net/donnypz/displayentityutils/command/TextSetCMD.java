package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

class TextSetCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.TEXT_SET_TEXT)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis text set <text>", NamedTextColor.RED));
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }
        if (partSelection.getSelectedParts().size() > 1){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with one part selected");
            return;
        }

        if (partSelection.getSelectedParts().getFirst().getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with text display entities");
            return;
        }
        TextDisplay display = (TextDisplay) partSelection.getSelectedParts().getFirst().getEntity();
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
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully set text on text display!");
        player.sendMessage(Component.text("Keep in mind, you can include \"\\n\" in your text display to create a new line.", NamedTextColor.GRAY));
    }
}
