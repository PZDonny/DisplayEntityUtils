package net.donnypz.displayentityutils.command.parts;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.donnypz.displayentityutils.utils.dialogs.TextDisplayDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Function;

class PartsCreateCMD extends PlayerSubCommand {

    private static final Component DEFAULT_TEXT = Component.text("New Text Display Entity");

    PartsCreateCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("create", parentSubCommand, Permission.PARTS_CREATE);
        setTabComplete(2, TabSuggestion.PART_TYPES);
        setTabComplete(3, "-addtogroup");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            incorrectUsage(player);
            return;
        }

        boolean addToGroup;
        if (args.length >= 4){
            addToGroup = args[3].equalsIgnoreCase("-addtogroup");
            if (addToGroup && DisplayGroupManager.getSelectedGroup(player) == null){
                DisplayEntityPluginCommand.noGroupSelection(player);
            }
        }
        else{
            addToGroup = false;
        }

        Location loc = player.getLocation();
        PacketAttributeContainer container = new PacketAttributeContainer();
        switch (args[2].toLowerCase()){
            case "block" -> {
                selectOrAddEntity(player, "Block Display", addToGroup, bukkit -> {
                    return loc.getWorld().spawn(loc, BlockDisplay.class, e -> {
                        e.setBlock(Registry.MATERIAL.get(NamespacedKey.minecraft("stone")).createBlockData());
                    });
                }, packet -> {
                    return container
                            .setAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE, Material.STONE.createBlockData())
                            .createPart(SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY, loc);
                });
            }
            case "item" -> {
                selectOrAddEntity(player, "Item Display", addToGroup, bukkit -> {
                    return loc.getWorld().spawn(loc, ItemDisplay.class, e -> {
                        e.setItemStack(new ItemStack(Material.STICK));
                    });
                }, packet -> {
                    return container
                            .setAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK, new ItemStack(Material.STICK))
                            .createPart(SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY, loc);
                });
            }
            case "text" -> {
                loc.setYaw(loc.getYaw()+180);
                loc.setPitch(loc.getPitch()*-1);

                ActivePart part = selectOrAddEntity(player, "Text Display", addToGroup, bukkit -> {
                    return loc.getWorld().spawn(loc, TextDisplay.class, e -> {
                        e.text(DEFAULT_TEXT);
                    });
                }, packet -> {
                    return container
                            .setAttribute(DisplayAttributes.TextDisplay.TEXT, DEFAULT_TEXT)
                            .createPart(SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY, loc);
                });
                if (part != null){
                    if (VersionUtils.canViewDialogs(player, false)){
                        player.sendMessage(Component.text("| Click here to edit it!", NamedTextColor.LIGHT_PURPLE)
                                .clickEvent(ClickEvent.callback(audience -> {
                                    Player p = (Player) audience;
                                    TextDisplayDialog.sendDialog(p, part, true);
                                }, ClickCallback.Options.builder().uses(-1).lifetime(Duration.ofMinutes(5)).build())));
                    }
                }
            }
            case "interaction" -> {
                selectOrAddEntity(player, "Interaction", addToGroup, bukkit -> {
                    return loc.getWorld().spawn(loc, Interaction.class, e -> {
                        e.setInteractionHeight(1);
                        e.setInteractionWidth(1);
                    });
                }, packet -> {
                    return container
                            .setAttribute(DisplayAttributes.Interaction.WIDTH, 1f)
                            .setAttribute(DisplayAttributes.Interaction.HEIGHT, 1f)
                            .createPart(SpawnedDisplayEntityPart.PartType.INTERACTION, loc);
                });
            }
            case "mannequin" -> {
                if (!VersionUtils.canSpawnMannequins()){
                    player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your server version cannot spawn Mannequins!", NamedTextColor.RED)));
                    return;
                }
                selectOrAddEntity(player, "Mannequin", addToGroup, bukkit -> {
                    return loc.getWorld().spawn(loc, Mannequin.class, m -> {
                        m.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
                        DisplayUtils.prepareMannequin(m);
                    });
                }, packet -> {
                    return container
                            .setAttribute(DisplayAttributes.Mannequin.RESOLVABLE_PROFILE, ResolvableProfile.resolvableProfile(player.getPlayerProfile()))
                            .setAttribute(DisplayAttributes.Mannequin.IMMOVABLE, true)
                            .setAttribute(DisplayAttributes.Mannequin.NO_GRAVITY, true)
                            .createPart(SpawnedDisplayEntityPart.PartType.MANNEQUIN, loc);
                });
            }
            default -> {
                incorrectUsage(player);
            }
        }
    }

    private void incorrectUsage(Player player){
        player.sendMessage(Component.text("Incorrect Usage! /deu parts create <block | item | text | interaction | mannequin>", NamedTextColor.RED));
    }

    private ActivePart selectOrAddEntity(Player player, String displayName, boolean addToGroup, Function<Void, Entity> bukkitEntity, Function<Void, PacketDisplayEntityPart> packetPart){
        player.sendMessage(DisplayAPI
                .pluginPrefix
                .append(MiniMessage.miniMessage().deserialize("<green>You have spawned a <aqua>" + displayName + " <green>entity!")));

        DEUUser user = DEUUser.getOrCreateUser(player);
        ActivePart part;
        if (addToGroup){
            ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
            if (group instanceof PacketDisplayEntityGroup pdeg){
                part = packetPart.apply(null);
                pdeg.addPart((PacketDisplayEntityPart) part);
            }
            else if (group instanceof SpawnedDisplayEntityGroup sdeg){
                part = sdeg.addEntity(bukkitEntity.apply(null));
            }
            else{
                return null;
            }
            ((MultiPartSelection<?>) user.getSelectedPartSelection()).refresh();
            player.sendMessage(Component.text("| The created part has been automatically added to your group", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        else{
            Entity entity = bukkitEntity.apply(null);
            player.sendMessage(Component.text("| The created part has been automatically selected, removing previous selections", NamedTextColor.GRAY, TextDecoration.ITALIC));
            part = SpawnedDisplayEntityPart.create(entity);
            user.setSelectedPartSelection(new SinglePartSelection((SpawnedDisplayEntityPart) part), false);
        }
        return part;
    }
}
