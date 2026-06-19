package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AnimCMD extends ParentSubCommand {

    public AnimCMD(){
        super("anim");
        this.subCommands.put("list", new ListCMD(
                Component.text("Incorrect Usage! /deu anim list <storage> [page-number]", NamedTextColor.RED),
                3,
                false));
        new AnimNewCMD(this);
        new AnimSaveCMD(this);
        new AnimSaveJsonCMD(this);
        new AnimDeleteCMD(this);
        new AnimInfoCMD(this);
        new AnimFrameInfoCMD(this);
        new AnimListActiveCMD(this);
        new AnimUseFilterCMD(this);
        new AnimUnfilterCMD(this);
        new AnimAddFrameCMD(this);
        new AnimAddFrameAfterCMD(this);
        new AnimRemoveFrameCMD(this);
        new AnimOverwriteFrameCMD(this);
        new AnimEditFrameCMD(this);
        new AnimAddPointCMD(this);
        new AnimShowPointsCMD(this);
        new AnimDrawPointsCMD(this);
        new AnimDrawPosCMD(this);
        new AnimCopyPointCMD(this);
        new AnimMovePointCMD(this);
        new AnimShowFrameCMD(this);
        new AnimPreviewFrameCMD(this);
        new AnimAddSoundCMD(this);
        new AnimAddDefaultSoundCMD(this);
        new AnimRemoveSoundCMD(this);
        new AnimAddParticleCMD(this);
        new AnimAddDefaultParticleCMD(this);
        new AnimReverseCMD(this);
        new AnimScaleRespectCMD(this);
        new AnimSetTagCMD(this);
        new AnimSetFrameTagCMD(this);
        new AnimPreviewPlayCMD(this);
        new AnimPlayCMD(this);
        new AnimStopCMD(this);
        new AnimRestoreCMD(this);
        new AnimSelectCMD(this);
        new AnimSelectJSONCMD(this);
    }

    static void noAnimationSelection(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You do not have an animation selected!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu anim select <animation-tag>", NamedTextColor.GRAY));
    }

    static void noFramePointSelection(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You do not have an frame point selected!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu anim frameinfo <frame-id>", NamedTextColor.GRAY));
    }

    static void hasNoFrames(Player player){
        player.sendMessage(Component.text("Your currently selected animation has no frames!", NamedTextColor.RED));
        player.sendMessage(Component.text("Use \"/deu anim addframe\" instead", NamedTextColor.GRAY));
    }
}