package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import com.destroystokyo.paper.ParticleBuilder;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@ApiStatus.Internal
public class AnimationParticleBuilder extends ParticleBuilder{
    Player player;
    Collection<FramePoint> framePoints;
    Step step;
    int delayInTicks = 0;
    AnimationParticle<?> editParticle = null;

    @ApiStatus.Internal
    public AnimationParticleBuilder(@NotNull Player player, @NotNull FramePoint framePoint){
        this(player, List.of(framePoint));
    }

    @ApiStatus.Internal
    public AnimationParticleBuilder(@NotNull Player player, @NotNull Collection<FramePoint> framePoints){
        super(Particle.FLAME);
        this.player = player;
        this.framePoints = new HashSet<>(framePoints);
        DEUUser.getOrCreateUser(player).setAnimationParticleBuilder(this);
        advanceStep(Step.PARTICLE);
    }

    @ApiStatus.Internal
    public AnimationParticleBuilder(@NotNull Player player, @NotNull AnimationParticle<?> editParticle, Step step){
        super(Particle.FLAME);
        this.player = player;
        DEUUser.getOrCreateUser(player).setAnimationParticleBuilder(this);
        advanceStep(step);
        this.editParticle = editParticle;
    }

    private AnimationParticleBuilder(Collection<FramePoint> framePoints, Particle particle){
        super(particle);
        this.framePoints = new HashSet<>(framePoints);
    }

    @ApiStatus.Internal
    public static AnimationParticleBuilder create(@NotNull FramePoint framePoint,
                                                  @NotNull Particle particle,
                                                  int count,
                                                  double xOffset,
                                                  double yOffset,
                                                  double zOffset,
                                                  double extra,
                                                  Object data){
        return create(List.of(framePoint), particle, count, xOffset, yOffset, zOffset, extra, data);
    }

    @ApiStatus.Internal
    public static AnimationParticleBuilder create(@NotNull Collection<FramePoint> framePoints,
                                                  @NotNull Particle particle,
                                                  int count,
                                                  double xOffset,
                                                  double yOffset,
                                                  double zOffset,
                                                  double extra,
                                                  Object data){
        AnimationParticleBuilder builder = new AnimationParticleBuilder(framePoints, particle);
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
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Particle Changes applied successfully!", NamedTextColor.GREEN)));
            }
            return;
        }
        if (nextStep == null){
            build();
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully created an animation particle!", NamedTextColor.GREEN)));
            DEUUser.getUser(player).removeAnimationParticleBuilder();
            return;
        }

        this.step = nextStep;
        nextStep.sendMessage(player);
    }

    public boolean isColorOnlyParticle(){
        return this.particle().getDataType().isAssignableFrom(Color.class);
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

    public static boolean isColorOnlyParticle(@NotNull Particle particle){
        return particle.getDataType().isAssignableFrom(Color.class);
    }

    public static boolean isBlockDataParticle(@NotNull Particle particle){
        return particle.getDataType().isAssignableFrom(BlockData.class);
    }

    public static boolean isItemParticle(@NotNull Particle particle){
        return particle == VersionUtils.getItemParticle();
    }

    public static boolean isDustOptionParticle(@NotNull Particle particle){
        return particle.getDataType().isAssignableFrom(Particle.DustOptions.class);
    }

    public static boolean isDustTransitionParticle(@NotNull Particle particle){
        return particle == Particle.DUST_COLOR_TRANSITION;
    }

    public static boolean isGeyserParticle(@NotNull Particle particle){
        return particle.getDataType().isAssignableFrom(Particle.Geyser.class);
    }

    public static boolean isGeyserBaseParticle(@NotNull Particle particle){
        return particle.getDataType().isAssignableFrom(Particle.GeyserBase.class);
    }

    public Step getStep() {
        return step;
    }


    @ApiStatus.Internal
    public void remove(){
        editParticle = null;
        if (framePoints != null) framePoints.clear();
        framePoints = null;
        player = null;
    }

    public AnimationParticle<?> build(){
        AnimationParticle<?> animParticle = getAnimationParticle();
        for (FramePoint fp : framePoints){
            fp.addParticle(animParticle.clone());
        }
        return animParticle;

    }

    public static Class<? extends AnimationParticle<?>> getAnimationParticleClass(@NotNull String particleName){
        try{
            return getAnimationParticleClass(Particle.valueOf(particleName));
        }
        catch(IllegalArgumentException e){
            return null;
        }
    }

    AnimationParticle<?> getAnimationParticle(){
        if (isBlockDataParticle()){
            return new BlockAnimationParticle(this, data());
        }
        else if (isItemParticle()){
            return new ItemStackAnimationParticle(this, data());
        }
        else if (isDustOptionParticle()){
            return new DustOptionAnimationParticle(this, data());
        }
        else if (isDustTransitionParticle()) {
            return new DustTransitionAnimationParticle(this, data());
        }
        else if (particle() == VersionUtils.getEntityEffectParticle()) {
            return new EntityEffectAnimationParticle(this, data());
        }
        else if (particle() == Particle.FLASH) {
            return new FlashAnimationParticle(this, data());
        }
        else if (isGeyserBaseParticle(particle())){
            return new GeyserBaseAnimationParticle(this, particle(), data());
        }
        else if (isGeyserParticle(particle())){
            return new GeyserAnimationParticle(this, particle(), data());
        }
        else {
            return new GeneralAnimationParticle(this, particle());
        }
    }

    public static Class<? extends AnimationParticle<?>> getAnimationParticleClass(@NotNull Particle particle){
        if (isBlockDataParticle(particle)){
            return BlockAnimationParticle.class;
        }
        else if (isItemParticle(particle)){
            return ItemStackAnimationParticle.class;
        }
        else if (isDustOptionParticle(particle)){
            return DustOptionAnimationParticle.class;
        }
        else if (isDustTransitionParticle(particle)) {
            return DustTransitionAnimationParticle.class;
        }
        else if (particle == VersionUtils.getEntityEffectParticle()) {
            return EntityEffectAnimationParticle.class;
        }
        else if (particle == Particle.FLASH) {
            return FlashAnimationParticle.class;
        }
        else if (isGeyserBaseParticle(particle)){
            return GeyserBaseAnimationParticle.class;
        }
        else if (isGeyserParticle(particle)){
            return GeyserAnimationParticle.class;
        }
        else {
            return GeneralAnimationParticle.class;
        }
    }


    private boolean updateParticle(){
        boolean result = editParticle.editParticle(this);
        DEUUser.getUser(player).removeAnimationParticleBuilder();
        return result;
    }


    public enum Step{
        PARTICLE("Enter the name of the particle to use", null),
        COUNT("Enter the amount of particles to spawn", null),
        EXTRA("Enter the extra value for the particle(s)", null),

        COLOR_AND_SIZE("Enter the color to set for the particle(s) and a particle size", "red 1"),
        COLOR_ONLY("Enter the color to set for the particle(s)", null),
        COLOR_TRANSITION("Enter the color transition to set for the particle(s) and a particle size", "red blue 2"),
        ITEM("Enter the item to use for the particle(s)", null),
        BLOCK("Enter the block to use for the particle(s).\nType \"-held\" to use your held block item, \"-target\" for your targeted block, or the block's id.", null),
        GEYSER("Enter the particle's water blocks", null),
        GEYSER_BASE("Enter the particle's water blocks and burst impulse", "4 3.5"),

        OFFSETS("Enter the x, y, and z offset for the particle(s)", "1.5 0 1.5"),
        DELAY("Enter the amount of delay (in ticks) before the particle should be shown", null);

        private final Component message;
        private final Component example;

        private static final Component separatedMSG = Component.text("All values should be entered separated by spaces.", NamedTextColor.GRAY, TextDecoration.ITALIC);
        Step(String message, String example){
            this.message = Component.text(message, NamedTextColor.YELLOW);
            this.example = example == null ? null : Component.text("Example: "+example, NamedTextColor.GRAY);
        }

        void sendMessage(Player player){
            player.sendMessage(message);
            if (example != null){
                player.sendMessage(separatedMSG);
                player.sendMessage(example);
            }
        }
    }
}
