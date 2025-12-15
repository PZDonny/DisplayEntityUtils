package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.group.GroupSpawnCMD;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimSelectCMD extends PlayerSubCommand {
    AnimSelectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("select", parentSubCommand, Permission.ANIM_SELECT);
        setTabComplete(2, "<anim-tag>");
        setTabComplete(3, TabSuggestion.STORAGES);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /deu anim select <anim-tag> <storage>", NamedTextColor.RED));
            return;
        }
        String tag = args[2];
        String storage = args[3];
        getAnimation(player, tag, storage);
    }

    static void getAnimation(Player p, String tag, String storage){
        if (storage.equals("all")){
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Attempting to select animation from all storage locations", NamedTextColor.YELLOW)));
            GroupSpawnCMD.attemptAll(p, tag, LoadMethod.LOCAL, false);
            return;
        }

        LoadMethod loadMethod;
        try{
            loadMethod = LoadMethod.valueOf(storage.toUpperCase());
        }
        catch(IllegalArgumentException e){
            p.sendMessage(Component.text("Invalid Storage Method!", NamedTextColor.RED));
            p.sendMessage(Component.text("Valid storage methods are local, mongodb, or mysql", NamedTextColor.GRAY));
            return;
        }
        if (!loadMethod.isEnabled()){
            p.sendMessage(Component.text("- Storage location is disabled and cannot be checked!", NamedTextColor.GRAY));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSpawnedDisplayAnimation(tag, loadMethod);
        if (anim == null){
            p.sendMessage(Component.text("- Failed to find saved display animation in that storage location!", NamedTextColor.RED));
            return;
        }

        DisplayAnimationManager.setSelectedSpawnedAnimation(p, anim);

        p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully selected animation! <white>(Tagged: "+anim.getAnimationTag()+")")));
        DisplayEntityPluginCommand.hideRelativePoints(p);
    }
}
