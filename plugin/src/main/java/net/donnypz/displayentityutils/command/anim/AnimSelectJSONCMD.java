package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

class AnimSelectJSONCMD extends PlayerSubCommand {
    AnimSelectJSONCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("selectjson", parentSubCommand, Permission.ANIM_SELECT);
        setTabComplete(2, "<file-name>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim selectjson <file-name>", NamedTextColor.RED));
            return;
        }
        String fileName = args[2];
        getAnimation(player, fileName);
    }

    static void getAnimation(Player p, String tag){
        String fileName = tag.endsWith(".json") ? tag : tag+".json";
        DisplayAnimation jsonAnim = DisplayAnimationManager.getAnimationFromJSON(new File(PluginFolders.animSaveFolder, "/"+fileName));
        if (jsonAnim == null){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>- Failed to find <light_purple>JSON <red>saved animation!"));
            return;
        }
        SpawnedDisplayAnimation anim = jsonAnim.toSpawnedDisplayAnimation();

        DisplayAnimationManager.setSelectedSpawnedAnimation(p, anim);

        p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully selected animation! <white>(Tagged: "+anim.getAnimationTag()+")")));

        if (RelativePointUtils.removeRelativePoints(p)){
            p.sendMessage(Component.text("Your previewed points have been despawned since you have changed your selected animation", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
    }
}
