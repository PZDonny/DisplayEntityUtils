package net.donnypz.displayentityutils.listeners.player;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DEUPlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event){
        Player p = event.getPlayer();
        AnimationParticleBuilder builder = AnimationParticleBuilder.getBuilder(p);
        if (builder == null){
            return;
        }
        AnimationParticleBuilder.Step step = builder.getStep();

        event.setCancelled(true);
        String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
        switch (step){
            case PARTICLE -> {
                try{
                    Particle particle = Particle.valueOf(msg);
                    builder.particle(particle);
                    builder.advanceStep(AnimationParticleBuilder.Step.COUNT);
                }
                catch(IllegalArgumentException ex){
                    p.sendMessage(Component.text("Invalid Particle!", NamedTextColor.RED));
                }
            }
            case COUNT -> {
                try{
                    int amount = Integer.parseInt(msg);
                    builder.count(amount);
                    if (builder.isDustOptionParticle()){
                        builder.advanceStep(AnimationParticleBuilder.Step.COLOR);
                    }
                    else if (builder.isDustTransitionParticle()){
                        builder.advanceStep(AnimationParticleBuilder.Step.COLOR_TRANSITION);
                    }
                    else if (builder.particle() == Particle.ENTITY_EFFECT){
                        builder.advanceStep(AnimationParticleBuilder.Step.COLOR_ENTITY_EFFECT);
                    }
                    else{
                        builder.advanceStep(AnimationParticleBuilder.Step.EXTRA);
                    }
                }
                catch(IllegalArgumentException ex){
                    p.sendMessage(Component.text("Invalid Particle Count! Enter a whole number greater than 0.", NamedTextColor.RED));
                }
            }
            case OFFSETS -> {
                try{
                    String[] offsets = msg.split(" ");
                    if (offsets.length != 3){
                        throw new NumberFormatException();
                    }
                    double xOffset = Double.parseDouble(offsets[0]);
                    double yOffset = Double.parseDouble(offsets[1]);
                    double zOffset = Double.parseDouble(offsets[2]);
                    builder.offset(xOffset, yOffset, zOffset);
                    builder.advanceStep(AnimationParticleBuilder.Step.DELAY);
                }
                catch(NumberFormatException e){
                    p.sendMessage(Component.text("Invalid Offset Value(s)! Enter 3 numbers.", NamedTextColor.RED));
                }
            }
            case DELAY -> {
                try{
                    builder.delay(Integer.parseInt(msg));
                    builder.advanceStep(null);
                }
                catch(NumberFormatException e){
                    p.sendMessage(Component.text("Invalid Delay Value! Enter a non-negative whole number.", NamedTextColor.RED));
                }
            }
            case EXTRA -> {
                try{
                    double amount = Double.parseDouble(msg);
                    builder.extra(amount);
                    if (builder.isBlockDataParticle()){
                        builder.advanceStep(AnimationParticleBuilder.Step.BLOCK);
                    }
                    else if (builder.isItemParticle()){
                        builder.advanceStep(AnimationParticleBuilder.Step.ITEM);
                    }
                    else{
                        builder.advanceStep(AnimationParticleBuilder.Step.OFFSETS);
                    }
                }
                catch(NumberFormatException ex){
                    p.sendMessage(Component.text("Invalid Extra Value! Enter a whole number greater than 0.", NamedTextColor.RED));
                }
            }
            case COLOR -> {
                try{
                    String[] args = msg.split(" ");
                    if (args.length != 2){
                        throw new IllegalArgumentException();
                    }
                    Color color = DEUCommandUtils.getColorFromText(args[0]);

                    if (color == null){
                        throw new IllegalArgumentException();
                    }
                    builder.color(color, Float.parseFloat(args[1]));
                    builder.advanceStep(AnimationParticleBuilder.Step.OFFSETS);
                }
                catch(IllegalArgumentException e){
                    p.sendMessage(Component.text("Invalid Color Value! Enter a color and a particle size.", NamedTextColor.RED));
                    p.sendMessage(Component.text("color size", NamedTextColor.GRAY, TextDecoration.ITALIC));
                }
            }
            case COLOR_ENTITY_EFFECT -> {
                try{
                    Color color = DEUCommandUtils.getColorFromText(msg);

                    if (color == null){
                        throw new IllegalArgumentException();
                    }
                    builder.data(color);
                    builder.advanceStep(AnimationParticleBuilder.Step.OFFSETS);
                }
                catch(IllegalArgumentException e){
                    p.sendMessage(Component.text("Invalid Color Value!", NamedTextColor.RED));
                }
            }
            case COLOR_TRANSITION -> {
                try{
                    String[] args = msg.split(" ");
                    if (args.length != 3){
                        throw new IllegalArgumentException();
                    }
                    Color c1 = DEUCommandUtils.getColorFromText(args[0]);
                    Color c2 = DEUCommandUtils.getColorFromText(args[1]);

                    if (c1 == null || c2 == null){
                        throw new IllegalArgumentException();
                    }
                    builder.colorTransition(c1, c2, Float.parseFloat(args[2]));
                    builder.advanceStep(AnimationParticleBuilder.Step.OFFSETS);
                }
                catch(IllegalArgumentException e){
                    p.sendMessage(Component.text("Invalid Color Values! Enter two colors and a particle size.", NamedTextColor.RED));
                    p.sendMessage(Component.text("color color size", NamedTextColor.GRAY, TextDecoration.ITALIC));
                }
            }
            case BLOCK -> {
                BlockData blockData = DEUCommandUtils.getBlockFromText(msg, p);
                if (blockData == null){
                    return;
                }
                builder.data(blockData);
                builder.advanceStep(AnimationParticleBuilder.Step.OFFSETS);
            }
            case ITEM -> {
                ItemStack item = DEUCommandUtils.getItemFromText(msg, p);
                if (item == null){
                    return;
                }
                builder.data(item);
                builder.advanceStep(AnimationParticleBuilder.Step.OFFSETS);
            }
        }
    }
}
