package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class GroupCMD extends ParentSubCommand{

    public GroupCMD(){
        super("group");
        this.subCommands.put("list", new ListCMD(
                Component.text("Incorrect Usage! /deu group list <storage> [page-number]", NamedTextColor.RED),
                3,
                true));
        new GroupSelectCMD(this);
        new GroupSelectNearestCMD(this);
        new GroupSelectPlacedCMD(this);
        new GroupDeselectCMD(this);
        new GroupSaveCMD(this);
        new GroupSaveJsonCMD(this);
        new GroupToPacketCMD(this);
        new GroupMarkPacketGroupsCMD(this);
        new GroupHidePersistentPacketGroupsCMD(this);
        new GroupShowPersistentPacketGroupsCMD(this);
        new GroupDeleteCMD(this);
        new GroupSpawnCMD(this);
        new GroupSpawnAtCMD(this);
        new GroupSpawnJSONCMD(this);
        new GroupDespawnCMD(this);
        new GroupDespawnAtCMD(this);
        new GroupInfoCMD(this);
        new GroupSetTagCMD(this);
        new GroupYawCMD(this);
        new GroupPitchCMD(this);
        new GroupScaleCMD(this);
        new GroupBrightnessCMD(this);
        new GroupMoveHereCMD(this);
        new GroupMoveCMD(this);
        new GroupTranslateCMD(this);
        new GroupUngroupInteractionsCMD(this);
        new GroupMergeCMD(this);
        new GroupAddTargetCMD(this);
        new GroupCloneCMD(this);
        new GroupGlowCMD(this);
        new GroupUnglowCMD(this);
        new GroupGlowColorCMD(this);
        new GroupSetSpawnAnimationCMD(this);
        new GroupUnsetSpawnAnimationCMD(this);
        new GroupPersistCMD(this);
        new GroupPersistenceOverrideCMD(this);
        new GroupBillboardCMD(this);
        new GroupViewRangeCMD(this);
        new GroupRideCMD(this);
        new GroupRideDespawnCMD(this);
        new GroupSafeDismountCMD(this);
        new GroupDismountCMD(this);
        new GroupWorldEditCMD(this);
        new GroupAutoCullCMD(this);
        new GroupRemoveCullCMD(this);
    }

    static void groupToPacketInfo(Player player){
        player.sendMessage(Component.text("| Selected groups can become packet-based with \"/deu group topacket\"", NamedTextColor.GRAY, TextDecoration.ITALIC));
    }
}
