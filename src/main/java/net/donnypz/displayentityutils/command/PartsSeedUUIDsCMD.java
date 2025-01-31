package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class PartsSeedUUIDsCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_SEED_UUIDS)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 4){
            player.sendMessage(Component.text("/mdis parts randomizeuuids <group | selection> <seed>", NamedTextColor.RED));
            return;
        }
        if (!args[2].equalsIgnoreCase("group") && !args[2].equalsIgnoreCase("selection")){
            player.sendMessage(Component.text("Invalid Option! Choose between group or selection", NamedTextColor.RED));
            player.sendMessage(Component.text("/mdis parts randomizeuuids <group | selection> <seed>", NamedTextColor.GRAY));
            return;
        }

        try{
            long seed = Long.parseLong(args[3]);
            if (args[2].equalsIgnoreCase("group")){
                group.seedPartUUIDs(seed);
                player.sendMessage(Component.text("Successfully randomized the part UUIDs of all parts in selected group!", NamedTextColor.GREEN));
                player.sendMessage(Component.text("Seed: "+seed, NamedTextColor.GRAY));
            }
            else{
                SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
                if (partSelection == null){
                    DisplayEntityPluginCommand.noPartSelection(player);
                    return;
                }
                partSelection.randomizePartUUIDs(seed);
                player.sendMessage(Component.text("Successfully randomized the part UUIDs of all parts in your part selection!", NamedTextColor.GREEN));
                player.sendMessage(Component.text("Seed: "+seed, NamedTextColor.GRAY));
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(Component.text("Invalid Seed Number! Enter a valid whole number", NamedTextColor.RED));
        }
    }

}
