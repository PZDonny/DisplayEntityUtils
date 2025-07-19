package net.donnypz.displayentityutils.command.anim;

import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

class AnimDrawPosCMD extends PlayerSubCommand {
    AnimDrawPosCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("drawpos", parentSubCommand, Permission.ANIM_DRAW_FRAME_POINTS);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            incorrectUsage(player);
            return;
        }

        DEUUser user = DEUUser.getOrCreateUser(player);
        try{
            int pos = Integer.parseInt(args[2]);

            user.setPointPos(player.getLocation(), pos);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set draw pos", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Pos: "+pos, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e) {
            if (!args[2].equalsIgnoreCase("show")) {
                incorrectUsage(player);
                return;
            }
            showPointPos(user, player);
        }
    }

    private void incorrectUsage(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis anim drawpos <1 | 2 | 3 | show>", NamedTextColor.RED)));
    }

    private void showPointPos(DEUUser user, Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Display point positions! (Click a point to teleport to it)", NamedTextColor.GREEN)));
        Location[] locs = user.getPointPositions(player.getWorld());
        for (int i = 0; i < 3; i++){
            Location l = locs[i];
            if (l == null){
                player.sendMessage(Component.text("| Pos "+i+": NOT SET", NamedTextColor.RED));
                continue;
            }
            l = l.clone();
            Particle particle;
            if (i == 0){
                particle = Particle.FLAME;
            }
            else if (i == 1){
                particle = Particle.SOUL_FIRE_FLAME;
            }
            else{
                particle = Particle.END_ROD;
            }
            Location finalL = l;
            player.sendMessage(Component.text("| Pos "+i+": "+particle.name(), NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.callback(clicker -> {
                        player.teleport(finalL, TeleportFlag.EntityState.RETAIN_PASSENGERS);
                    }, ClickCallback.Options
                            .builder()
                            .uses(ClickCallback.UNLIMITED_USES)
                            .lifetime(Duration.ofSeconds(60*60))
                            .build())));

            new BukkitRunnable(){
                int timesShown;
                @Override
                public void run() {
                    if (timesShown == 40){
                        cancel();
                        return;
                    }
                    player.spawnParticle(particle, finalL,1 ,0 ,0 ,0, 0);
                    timesShown++;
                }
            }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 2);
        }
    }
}
