package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class AnimSelectCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_SELECT)){
            return;
        }

        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim select <anim-tag> <storage>", NamedTextColor.RED));
            return;
        }
        String tag = args[2];
        String storage = args[3];
        getAnimation(player, tag, storage);
    }

    static void getAnimation(Player p, String tag, String storage){
        if (storage.equals("all")){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Attempting to spawn display from all storage locations");
            GroupSpawnCMD.attemptAll(p, tag, LoadMethod.LOCAL, false);
            return;
        }

        LoadMethod loadMethod;
        try{
            loadMethod = LoadMethod.valueOf(storage.toUpperCase());
        }
        catch(IllegalArgumentException e){
            p.sendMessage(Component.text("Invalid Storage Method!", NamedTextColor.RED));
            p.sendMessage(Component.text("Valid storage methods are local, mongodb, or mysql"));
            return;
        }
        if (!loadMethod.isEnabled()){
            p.sendMessage(Component.text("- Storage location is disabled and cannot be checked!", NamedTextColor.GRAY));
            return;
        }
        DisplayAnimation anim = DisplayAnimationManager.retrieve(loadMethod, tag);
        if (anim == null){
            p.sendMessage(Component.text("- Failed to find saved display animation in that storage location!", NamedTextColor.RED));
            return;
        }
        DisplayAnimationManager.setSelectedSpawnedAnimation(p, anim.toSpawnedDisplayAnimation());
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully selected display animation! "+ChatColor.WHITE+"(Tagged: "+tag+")");
    }
}
