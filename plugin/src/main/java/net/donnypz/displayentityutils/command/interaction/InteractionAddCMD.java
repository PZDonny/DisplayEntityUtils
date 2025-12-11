package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class InteractionAddCMD extends PlayerSubCommand {
    InteractionAddCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addcmd", parentSubCommand, Permission.INTERACTION_ADD_CMD);
        setTabComplete(2, List.of("player", "console"));
        setTabComplete(3, List.of("left", "right", "both"));
        setTabComplete(4, "<command>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 5){
            player.sendMessage(Component.text("/deu interaction addcmd <player | console> <left | right | both> <command>", NamedTextColor.RED));
            return;
        }
        String executor = args[2].toLowerCase();
        boolean isConsole;
        if (executor.equals("console")){
            isConsole = true;
        }
        else if (executor.equals("player")){
            isConsole = false;
        }
        else{
            player.sendMessage(Component.text("Invalid Command Executor!", NamedTextColor.RED));
            player.sendMessage(Component.text("Pick between \"player\" or \"console\"", NamedTextColor.GRAY));
            return;
        }

        String click = args[3].toLowerCase();
        boolean isLeftClick;
        boolean isBoth;
        if (click.equals("left")){
            isLeftClick = true;
            isBoth = false;
        }
        else if (click.equals("right")){
            isLeftClick = false;
            isBoth = false;
        }
        else if (click.equals("both")){
            isBoth = true;
            isLeftClick = false;
        }
        else{
            player.sendMessage(Component.text("Invalid Click Type!", NamedTextColor.RED));
            player.sendMessage(Component.text("Pick between \"left\", \"right\", or \"both\"", NamedTextColor.GRAY));
            return;
        }

        InteractionCMD.SelectedInteraction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 4; i < args.length; i++){
            builder.append(args[i]);
            if (i+1 != args.length) builder.append(" ");
        }
        String command = builder.toString();
        if (isBoth){
            interaction.addInteractionCommand(command, true, isConsole);
            interaction.addInteractionCommand(command, false, isConsole);
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Command Added! <yellow>("+command+")")));
        }
        else{
            interaction.addInteractionCommand(command, isLeftClick, isConsole);
            int cmdID = interaction.getInteractionCommands().size()-1;
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Command Added! <yellow>(ID: "+cmdID+" | "+command+")")));
        }
    }
}
