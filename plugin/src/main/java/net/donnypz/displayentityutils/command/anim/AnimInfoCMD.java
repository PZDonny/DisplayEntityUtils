package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PartFilter;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimInfoCMD extends PlayerSubCommand {
    AnimInfoCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("info", parentSubCommand, Permission.ANIM_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_INFO)){
            return;
        }
        SpawnedDisplayAnimation animation = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (animation == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefixLong);

        String animTag = animation.getAnimationTag() == null ? "<red>NOT SET" : "<yellow>"+animation.getAnimationTag();
        player.sendMessage(MiniMessage.miniMessage().deserialize("Animation Tag: "+animTag));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Total Frames: <yellow>"+animation.getFrames().size()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Total Duration: <yellow>"+animation.getDuration()+" ticks"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Respect Scale: <yellow>"+animation.groupScaleRespect()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Allows Texture Changes: <yellow>"+animation.allowsTextureChanges()));

        if (!animation.hasFilter()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<white>Animation Part Filter: <red>NOT SET"));
            return;
        }

        player.sendMessage(Component.empty());
        player.sendMessage("Animation Part Filter:");
        PartFilter filter = animation.getFilter();

        player.sendMessage(Component.text("| Included Part Tags", NamedTextColor.GRAY));
        for (String s : filter.getIncludedPartTags()){
            player.sendMessage(Component.text("- "+s, NamedTextColor.YELLOW));
        }

        player.sendMessage(Component.text("| Excluded Part Tags:", NamedTextColor.GRAY));
        for (String s : filter.getExcludedPartTags()){
            player.sendMessage(Component.text("- "+s, NamedTextColor.YELLOW));
        }

        player.sendMessage(Component.text("| Part Types:", NamedTextColor.GRAY));
        for (SpawnedDisplayEntityPart.PartType type : filter.getPartTypes()){
            player.sendMessage(Component.text("- "+type, NamedTextColor.YELLOW));
        }

        if (filter.isIncludingBlockTypes()){
            player.sendMessage(Component.text("| Block Types (Included):", NamedTextColor.GRAY));
        }
        else{
            player.sendMessage(Component.text("| Block Types (Excluded):", NamedTextColor.GRAY));
        }

        for (Material type: filter.getBlockTypes()){
            player.sendMessage(Component.text("- "+type.getKey().asMinimalString(), NamedTextColor.YELLOW));
        }

        if (filter.isIncludingItemTypes()){
            player.sendMessage(Component.text("| Item Types (Included):", NamedTextColor.GRAY));
        }
        else{
            player.sendMessage(Component.text("| Item Types (Excluded):", NamedTextColor.GRAY));
        }

        for (Material type : filter.getItemTypes()){
            player.sendMessage(Component.text("- "+type.getKey().asMinimalString(), NamedTextColor.YELLOW));
        }
    }
}
