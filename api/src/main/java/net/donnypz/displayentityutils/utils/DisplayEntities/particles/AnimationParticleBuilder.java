package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import com.destroystokyo.paper.ParticleBuilder;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class AnimationParticleBuilder extends ParticleBuilder{
    Player player;
    FramePoint framePoint;
    Step step;
    int delayInTicks = 0;
    AnimationParticle editParticle = null;

    private static final Component prefix = DisplayAPI.pluginPrefix;
    private static final Component particleMSG = prefix.append(Component.text("Enter the name of the particle to use", NamedTextColor.YELLOW));
    private static final Component amountMSG = prefix.append(Component.text("Enter the amount of particles to spawn", NamedTextColor.YELLOW));
    private static final Component colorAndSizeMSG = prefix.append(Component.text("Enter the color to set for the particle(s) and a particle size", NamedTextColor.YELLOW));
    private static final Component colorMSG = prefix.append(Component.text("Enter the color to set for the particle(s)", NamedTextColor.YELLOW));
    private static final Component colorTransitionMSG = prefix.append(Component.text("Enter the color transition to set for the particle(s) and a particle size", NamedTextColor.YELLOW));
    private static final Component extraMSG = prefix.append(Component.text("Enter the extra value for the particle(s)", NamedTextColor.YELLOW));
    private static final Component blockMSG = prefix.append(Component.text("Enter the block to use for the particle(s).\nType \"-held\" to use your held block item, \"-target\" for your targeted block, or the block's id.", NamedTextColor.YELLOW));
    private static final Component itemMSG = prefix.append(Component.text("Enter the item to use for the particle(s)", NamedTextColor.YELLOW));
    private static final Component offsetMSG = prefix.append(Component.text("Enter the x, y, and z offset for the particle(s)", NamedTextColor.YELLOW));
    private static final Component delayMSG = prefix.append(Component.text("Enter the amount of delay (in ticks) before the particle should be shown", NamedTextColor.YELLOW));
    private static final Component separatedMSG = Component.text("All values should be entered separated by spaces.", NamedTextColor.GRAY, TextDecoration.ITALIC);

    @ApiStatus.Internal
    public AnimationParticleBuilder(@NotNull Player player, @NotNull FramePoint framePoint){
        super(Particle.FLAME);
        this.player = player;
        this.framePoint = framePoint;
        DEUUser.getOrCreateUser(player).setAnimationParticleBuilder(this);
        advanceStep(Step.PARTICLE);
    }

    @ApiStatus.Internal
    public AnimationParticleBuilder(@NotNull Player player, @NotNull AnimationParticle editParticle, Step step){
        super(Particle.FLAME);
        this.player = player;
        DEUUser.getOrCreateUser(player).setAnimationParticleBuilder(this);
        advanceStep(step);
        this.editParticle = editParticle;
    }

    private AnimationParticleBuilder(FramePoint framePoint, Particle particle){
        super(particle);
        this.framePoint = framePoint;
    }

    @ApiStatus.Internal
    public static AnimationParticleBuilder create(@NotNull FramePoint framePoint, @NotNull Particle particle, int count, double xOffset, double yOffset, double zOffset, double extra, Object data){
        AnimationParticleBuilder builder = new AnimationParticleBuilder(framePoint, particle);
        builder
                .count(count)
                .extra(extra)
                .offset(xOffset, yOffset, zOffset)
                .data(data);
        return builder;
    }

    public void delay(int delayInTicks){
        this.delayInTicks = delayInTicks;
    }

    public void advanceStep(Step nextStep){
        if (editParticle != null){
            Player player = this.player;
            if (updateParticle()){
                player.sendMessage(prefix.append(Component.text("Particle Changes applied successfully!", NamedTextColor.GREEN)));
            }
            return;
        }
        if (nextStep == null){
            build();
            player.sendMessage(prefix.append(Component.text("Successfully created an animation particle!", NamedTextColor.GREEN)));
            DEUUser.getUser(player).removeAnimationParticleBuilder();
            return;
        }

        this.step = nextStep;
        switch(nextStep){
            case PARTICLE -> {
                player.sendMessage(particleMSG);
            }
            case COUNT -> {
                player.sendMessage(amountMSG);
            }
            case COLOR_AND_SIZE -> {
                player.sendMessage(colorAndSizeMSG);
                player.sendMessage(separatedMSG);
            }
            case COLOR_ONLY -> {
                player.sendMessage(colorMSG);
            }
            case COLOR_TRANSITION -> {
                player.sendMessage(colorTransitionMSG);
                player.sendMessage(separatedMSG);
                player.sendMessage(Component.text("Example: red blue 2", NamedTextColor.GRAY));
            }
            case EXTRA -> {
                player.sendMessage(extraMSG);
            }
        //Block
            case BLOCK -> {
                player.sendMessage(blockMSG);
            }
        //Item
            case ITEM -> {
                player.sendMessage(itemMSG);
            }
            case OFFSETS -> {
                player.sendMessage(offsetMSG);
                player.sendMessage(separatedMSG);
            }
            case DELAY -> {
                player.sendMessage(delayMSG);
            }
        }
    }


    public boolean isBlockDataParticle(){
        return this.particle().getDataType().isAssignableFrom(BlockData.class);
    }

    public boolean isItemParticle(){
        return this.particle() == VersionUtils.getItemParticle();
    }

    public boolean isDustOptionParticle(){
        return this.particle().getDataType().isAssignableFrom(Particle.DustOptions.class);
    }

    public boolean isDustTransitionParticle(){
        return this.particle() == Particle.DUST_COLOR_TRANSITION;
    }

    public Step getStep() {
        return step;
    }


    @ApiStatus.Internal
    public void remove(){
        editParticle = null;
        framePoint = null;
        player = null;
    }

    public AnimationParticle build(){
        AnimationParticle animParticle;
        if (isBlockDataParticle()){
            animParticle = new BlockAnimationParticle(this, data());
        }
        else if (isItemParticle()){
            animParticle = new ItemStackAnimationParticle(this, data());
        }
        else if (isDustOptionParticle()){
            animParticle = new DustOptionAnimationParticle(this, data());
        }
        else if (isDustTransitionParticle()){
            animParticle = new DustTransitionAnimationParticle(this, data());
        }
        else if (particle() == Particle.ENTITY_EFFECT){
            animParticle = new EntityEffectAnimationParticle(this, data());
        }
        else{
            animParticle = new GeneralAnimationParticle(this, particle());
        }

        animParticle.setDelayInTicks(delayInTicks);
        framePoint.addParticle(animParticle);

        return animParticle;
    }

    private boolean updateParticle(){
        boolean result = editParticle.editParticle(this);
        DEUUser.getUser(player).removeAnimationParticleBuilder();
        return result;
    }


    public enum Step{
        PARTICLE,
        COUNT,
        COLOR_AND_SIZE,
        COLOR_ONLY,
        COLOR_TRANSITION,
        EXTRA,
        ITEM,
        BLOCK,
        OFFSETS,
        DELAY
    }
}
