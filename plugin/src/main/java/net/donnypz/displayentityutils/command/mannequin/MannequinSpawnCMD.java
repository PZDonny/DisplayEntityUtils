package net.donnypz.displayentityutils.command.mannequin;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.CMDUtils;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class MannequinSpawnCMD extends PlayerSubCommand {
    MannequinSpawnCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawn", parentSubCommand, Permission.MANNEQUIN_SPAWN);
        setTabComplete(2, List.of("-g"));
    }

    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect ALL usage! /deu mannequin spawn [-g]", NamedTextColor.RED)));
    }

    @Override
    public void execute(Player player, String[] args) {
        Location location = player.getLocation();
        Mannequin mannequin = location.getWorld().spawn(location, Mannequin.class, m -> {
            m.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
            m.customName(Component.text("New Mannequin"));
            m.setCustomNameVisible(true);
        });
        DisplayUtils.prepareMannequin(mannequin);

        player.sendMessage(DisplayAPI.pluginPrefix
                .append(MiniMessage.miniMessage().deserialize("<green>A new mannequin has been spawned at your location!")));
        CMDUtils.tryAddEntityToGroup(player, mannequin, args, 2);
    }
}
