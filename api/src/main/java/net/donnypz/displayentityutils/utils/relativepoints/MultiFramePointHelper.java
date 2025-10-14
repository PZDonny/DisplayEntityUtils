package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiFramePointHelper {

    public static void addArcPoints(@NotNull ActiveGroup<?> group,
                                           @NotNull SpawnedDisplayAnimation animation,
                                           @NotNull Location loc1,
                                           @NotNull Location loc2,
                                           @NotNull Location loc3,
                                           int startFrame,
                                           int endFrame,
                                           int pointsPerFrame,
                                           @NotNull String pointTag){
        addArcPoints(group, animation, loc1, loc2, loc3, startFrame, endFrame, pointsPerFrame, pointTag, null);
    }

    public static void addArcPoints(@NotNull ActiveGroup<?> group,
                                    @NotNull SpawnedDisplayAnimation animation,
                                    @NotNull Location loc1,
                                    @NotNull Location loc2,
                                    @NotNull Location loc3,
                                    int startFrame,
                                    int endFrame,
                                    int pointsPerFrame,
                                    @NotNull String pointTag,
                                    @Nullable Player player){
        calculateArcPoints(group, animation, loc1, loc2, loc3, startFrame, endFrame, pointsPerFrame, pointTag, player, true);
    }

    public static void previewArcPoints(@NotNull ActiveGroup<?> group,
                                        @NotNull SpawnedDisplayAnimation animation,
                                        @NotNull Location loc1,
                                        @NotNull Location loc2,
                                        @NotNull Location loc3,
                                        int startFrame,
                                        int endFrame,
                                        int pointsPerFrame,
                                        @NotNull Player player){
        calculateArcPoints(group, animation, loc1, loc2, loc3, startFrame, endFrame, pointsPerFrame, "", player, false);
    }

    private static void calculateArcPoints(@NotNull ActiveGroup<?> group,
                                        @NotNull SpawnedDisplayAnimation animation,
                                        @NotNull Location loc1,
                                        @NotNull Location loc2,
                                        @NotNull Location loc3,
                                        int startFrame,
                                        int endFrame,
                                        int pointsPerFrame,
                                        @NotNull String pointTag,
                                        @Nullable Player player,
                                        boolean addToFrame){
        Location controlPoint = adjustedControlPoint(loc1, loc2, loc3);
        int diff = endFrame-startFrame;
        int totalPoints = diff*pointsPerFrame;
        double percentagePerPoint = 1.0/totalPoints;
        double percentage = 0;
        for (int i = startFrame; i <= endFrame; i++){
            SpawnedDisplayAnimationFrame frame = animation.getFrame(i);
            for (int ppf = 0; ppf < pointsPerFrame; ppf++){
                Location loc = quadraticBezier(loc1, controlPoint, loc3, percentage);
                percentage+=percentagePerPoint;

                if (addToFrame){
                    if (pointsPerFrame == 1){
                        frame.addFramePoint(pointTag, group, loc);
                    }
                    if (i == endFrame){
                        frame.addFramePoint(pointTag, group, loc);
                        pointParticle(loc, player);
                        break;
                    }
                    else{
                        frame.addFramePoint(pointTag+"_"+ppf, group, loc);
                    }
                }


                if (player != null) pointParticle(loc, player);
            }
        }
    }

    private static Location adjustedControlPoint(Location loc1, Location loc2, Location loc3) {
        double x = 2 * loc2.getX() - 0.5 * (loc1.getX() + loc3.getX());
        double y = 2 * loc2.getY() - 0.5 * (loc1.getY() + loc3.getY());
        double z = 2 * loc2.getZ() - 0.5 * (loc1.getZ() + loc3.getZ());

        return new Location(loc1.getWorld(), x, y, z);
    }

    public static Location quadraticBezier(Location loc1, Location loc2, Location loc3, double percentageFromLast) {
        double x = Math.pow(1 - percentageFromLast, 2) * loc1.getX()
                + 2 * (1 - percentageFromLast) * percentageFromLast * loc2.getX()
                + Math.pow(percentageFromLast, 2) * loc3.getX();

        double y = Math.pow(1 - percentageFromLast, 2) * loc1.getY()
                + 2 * (1 - percentageFromLast) * percentageFromLast * loc2.getY()
                + Math.pow(percentageFromLast, 2) * loc3.getY();

        double z = Math.pow(1 - percentageFromLast, 2) * loc1.getZ()
                + 2 * (1 - percentageFromLast) * percentageFromLast * loc2.getZ()
                + Math.pow(percentageFromLast, 2) * loc3.getZ();

        return new Location(loc1.getWorld(), x, y, z);
    }


    public static void addLinearPoints(@NotNull ActiveGroup<?> group,
                                       @NotNull SpawnedDisplayAnimation animation,
                                       @NotNull Location loc1,
                                       @NotNull Location loc2,
                                       int startFrame,
                                       int endFrame,
                                       int pointsPerFrame,
                                       @NotNull String pointTag){
        addLinearPoints(group , animation, loc1, loc2, startFrame, endFrame, pointsPerFrame, pointTag, null);
    }

    public static void addLinearPoints(@NotNull ActiveGroup<?> group,
                                       @NotNull SpawnedDisplayAnimation animation,
                                       @NotNull Location loc1,
                                       @NotNull Location loc2,
                                       int startFrame,
                                       int endFrame,
                                       int pointsPerFrame,
                                       @NotNull String pointTag,
                                       @Nullable Player player){
        calculateLinearPoints(group, animation, loc1, loc2, startFrame, endFrame, pointsPerFrame, pointTag, player, true);
    }

    public static void previewLinearPoints(@NotNull ActiveGroup<?> group,
                                           @NotNull SpawnedDisplayAnimation animation,
                                           @NotNull Location loc1,
                                           @NotNull Location loc2,
                                           int startFrame,
                                           int endFrame,
                                           int pointsPerFrame,
                                           @NotNull Player player){
        calculateLinearPoints(group, animation, loc1, loc2, startFrame, endFrame, pointsPerFrame, "", player, false);
    }

    private static void calculateLinearPoints(@NotNull ActiveGroup<?> group,
                                              @NotNull SpawnedDisplayAnimation animation,
                                              @NotNull Location loc1,
                                              @NotNull Location loc2,
                                              int startFrame,
                                              int endFrame,
                                              int pointsPerFrame,
                                              @NotNull String pointTag,
                                              @Nullable Player player,
                                              boolean addToFrame){
        Location loc = loc1.clone();
        double dist = loc1.distance(loc2);
        int diff = endFrame-startFrame;
        double evenDist = dist/diff/pointsPerFrame;

        Vector v = loc2.toVector().subtract(loc1.toVector()).normalize().multiply(evenDist);
        for (int i = startFrame; i <= endFrame; i++){
            SpawnedDisplayAnimationFrame frame = animation.getFrame(i);
            for (int ppf = 0; ppf < pointsPerFrame; ppf++){
                if (addToFrame){
                    if (pointsPerFrame == 1 || i == endFrame){
                        frame.addFramePoint(pointTag, group, loc);
                    }
                    else{
                        frame.addFramePoint(pointTag+"_"+ppf, group, loc);
                    }
                }

                if (player != null) pointParticle(loc, player);

                loc.add(v);
            }
        }
    }

    private static void pointParticle(Location location, Player player){
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if (i == 70){
                    cancel();
                    return;
                }
                player.spawnParticle(Particle.END_ROD, location, 1, 0,0,0,0);
                i++;
            }
        }.runTaskTimer(DisplayAPI.getPlugin(), 0, 2);
    }
}
