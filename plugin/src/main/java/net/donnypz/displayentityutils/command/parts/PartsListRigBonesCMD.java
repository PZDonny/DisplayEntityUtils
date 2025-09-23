package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

class PartsListRigBonesCMD extends PlayerSubCommand {
    PartsListRigBonesCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("listrigbones", parentSubCommand, Permission.PARTS_LIST_RIG_BONES);
    }

    @Override
    public void execute(Player player, String[] args) {

        ServerSideSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (!partSelection.isValid()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        List<String> bones;
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Part's Rig Bones:", NamedTextColor.YELLOW));
        bones = partSelection.getSelectedPart().getBones();


        if (bones.isEmpty()){
            player.sendMessage(Component.text("- No rig bones", NamedTextColor.GRAY));
        }
        else{
            for (String s : bones){
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- <yellow>"+s));
            }
        }
    }
}
