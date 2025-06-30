package net.donnypz.displayentityutils.listeners.entity;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.events.InteractionClickEvent;
import net.donnypz.displayentityutils.events.PreInteractionClickEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.command.RelativePointDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.List;

public class DEUInteractionListener implements Listener {

    //Right Click
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void rClick(PlayerInteractEntityEvent e){
        if (e.getRightClicked() instanceof Interaction entity){
            determineAction(entity, e.getPlayer(), InteractionClickEvent.ClickType.RIGHT);
        }
    }

    //Left Click
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void lClick(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Interaction entity){
            determineAction(entity, (Player) e.getDamager(), InteractionClickEvent.ClickType.LEFT);
        }
    }

    private void determineAction(Interaction interaction, Player player, InteractionClickEvent.ClickType clickType){
        //Point Displays
        if (RelativePointDisplay.isRelativePointEntity(interaction)){
            RelativePointDisplay point = RelativePointDisplay.get(interaction.getUniqueId());
            if (point == null){
                player.sendMessage(Component.text("Failed to get point!", NamedTextColor.RED));
                return;
            }

            //Left Click Action
            if (clickType == InteractionClickEvent.ClickType.LEFT){
                point.leftClick(player);
                DEUCommandUtils.selectRelativePoint(player, point);
                return;
            }

            //Right Click Action
            if (!player.isSneaking()){
                point.rightClick(player);
                return;
            }

            //Right Click Action (Remove Point)
            if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_REMOVE_FRAME_POINT)){
                return;
            }

            player.sendMessage(buildPointRemovalComponent(point));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        }

        if (!new PreInteractionClickEvent(player, interaction, clickType).callEvent()){
            return;
        }

        List<InteractionCommand> commands = DisplayUtils.getInteractionCommandsWithData(interaction);
        InteractionClickEvent event = new InteractionClickEvent(player, interaction, clickType, commands);

        if (!event.callEvent()){
            return;
        }

        //Execute Commands
        Player p = event.getPlayer();
        for (InteractionCommand cmd : event.getCommands()){
            if (cmd.isLeftClick() && event.getClickType() == InteractionClickEvent.ClickType.LEFT){
                runCommand(cmd, p);
            }
            else if (!cmd.isLeftClick() && event.getClickType() == InteractionClickEvent.ClickType.RIGHT){
                runCommand(cmd, p);
            }
        }
    }

    private Component buildPointRemovalComponent(RelativePointDisplay point){
        return Component.text("Click here to confirm point REMOVAL", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.callback(a -> {
                    Player p = (Player) a;
                    boolean result = point.removeFromPointHolder();
                    DEUCommandUtils.removeRelativePoint(p, point);
                    if (result){
                        p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully removed point from frame!", NamedTextColor.YELLOW)));
                        point.despawn();
                    }
                    else{
                        p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("This point has already been removed by another player or other methods!", NamedTextColor.RED)));
                    }
                }));
    }

    private void runCommand(InteractionCommand command, Player player){
        if (!command.isConsoleCommand()){
            player.performCommand(command.getCommand());
        }
        else{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getCommand());
        }
    }
}
