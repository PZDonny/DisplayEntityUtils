package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.VersionUtils;
import net.donnypz.displayentityutils.utils.dialogs.TextDisplayDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

class PartsCreateCMD extends PlayerSubCommand {

    PartsCreateCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("create", parentSubCommand, Permission.PARTS_CREATE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            incorrectUsage(player);
            return;
        }

        Location loc = player.getLocation();
        switch (args[2].toLowerCase()){
            case "block" -> {
                BlockDisplay entity = loc.getWorld().spawn(loc, BlockDisplay.class, e -> {
                    e.setBlock(BlockType.STONE.createBlockData());
                });
                selectEntity(player, entity.getUniqueId(), "Block Display");
            }
            case "item" -> {
                ItemDisplay entity = loc.getWorld().spawn(loc, ItemDisplay.class, e -> {
                    e.setItemStack(ItemStack.of(Material.STICK));
                });
                selectEntity(player, entity.getUniqueId(), "Item Display");
            }
            case "text" -> {
                TextDisplay entity = loc.getWorld().spawn(loc, TextDisplay.class, e -> {
                    e.text(Component.text("New Text Display Entity"));
                });
                selectEntity(player, entity.getUniqueId(), "Text Display");
                if (VersionUtils.canViewDialogs(player, false)){
                    UUID entityUUID = entity.getUniqueId();
                    player.sendMessage(Component.text("| Click here to edit it!", NamedTextColor.LIGHT_PURPLE)
                            .clickEvent(ClickEvent.callback(audience -> {
                                Player p = (Player) audience;
                                TextDisplayDialog.sendDialog(p, entityUUID, true);
                            }, ClickCallback.Options.builder().uses(-1).lifetime(Duration.ofMinutes(5)).build())));
                }
            }
            case "interaction" -> {
                Interaction entity = loc.getWorld().spawn(loc, Interaction.class, e -> {
                    e.setInteractionHeight(1);
                    e.setInteractionWidth(1);
                });
                selectEntity(player, entity.getUniqueId(), "Interaction");
            }
            default -> {
                incorrectUsage(player);
            }
        }
    }

    private void incorrectUsage(Player player){
        player.sendMessage(Component.text("Incorrect Usage! /mdis parts create <block | item | text | interaction>", NamedTextColor.RED));
    }

    private void selectEntity(Player player, UUID entityUUID, String displayName){
        player.sendMessage(DisplayEntityPlugin
                .pluginPrefix
                .append(MiniMessage.miniMessage().deserialize("<green>You have spawned a <aqua>" + displayName + " <green>entity at your location!")));
        DEUUser.getOrCreateUser(player).setSelectedPartSelection(new SinglePartSelection(SpawnedDisplayEntityPart.create(entityUUID)), false);
    }

}
