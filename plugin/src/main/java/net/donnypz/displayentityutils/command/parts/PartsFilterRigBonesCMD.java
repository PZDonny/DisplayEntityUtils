package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsFilterRigBonesCMD extends PlayerSubCommand {
    PartsFilterRigBonesCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("filterrigbones", parentSubCommand, Permission.PARTS_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ServerSideSelection sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = (SpawnedPartSelection) sel;
        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts filterrigbones <rig-bone-names>", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.empty());
        PartFilter builder = new PartFilter();
        if (args[2].equals("!")){
            player.sendMessage(Component.text("You cannot do that!"));
            return;
        }
        for (String tag : args[2].split(",")){
            if (!tag.startsWith("!")){
                builder.includeRigBone(tag);
                player.sendMessage(Component.text("Including rig bone: "+tag, NamedTextColor.GREEN));
            }
            else{
                String excludedTag = tag.substring(1);
                builder.excludeRigBone(excludedTag);
                player.sendMessage(Component.text("Excluding rig bone: "+excludedTag, NamedTextColor.RED));
            }
        }

        partSelection.unfilter(PartFilter.FilterType.INCLUDED_RIG_BONES, false);
        partSelection.unfilter(PartFilter.FilterType.EXCLUDED_RIG_BONES, false);

        if (!partSelection.applyFilter(builder, false)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to apply filter!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Rig Bone Filters Applied!")));
        partSelection.glow(player, 30);
    }

}
