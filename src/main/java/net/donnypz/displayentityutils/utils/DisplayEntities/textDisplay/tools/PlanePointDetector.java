package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Detects which players are looking at a rectangular plane (e.g. a TextDisplay pixel).
 * <p>
 * Usage:
 *
 * <pre>{@code
 * Vector anchor        = pixel.getLocation().toVector();      // world position of the display entity
 * Matrix4f localMatrix = pixel.getTextDisplayMatrix4f();      // local T * R * S (no world offset)
 *
 * PlanePointDetector detector = new PlanePointDetector(
 *         Bukkit.getOnlinePlayers(),                         // players to test
 *         anchor                                            // world-space anchor
 * );
 *
 * List<Player> looking = detector.lookingAt(localMatrix);
 * }</pre>
 */
public class PlanePointDetector {
    /* ────────────────────────────────────────────────────────────────────────── */
    /*  Public API                                                               */
    /* ────────────────────────────────────────────────────────────────────────── */

    /**
     * @param players          players to consider each tick
     * @param displayPosition  world position of the plane’s origin (same as the Display entity’s location)
     * @param xRange           local X bounds you consider “inside” – default 0‥1
     * @param yRange           local Y bounds – default 0‥1
     */

    public PlanePointDetector(Collection<? extends Player> players,
                              Vector displayPosition,
                              Range xRange,
                              Range yRange) {

        this.displayPosition = displayPosition.clone();
        this.xRange = xRange;
        this.yRange = yRange;

        // Pre-bake each player’s ray (eye → eye+direction) relative to the plane’s anchor
        this.points = players.stream()
                .map(p -> {
                    Vector eye     = p.getEyeLocation().toVector();
                    Vector3f p1    = eye.clone().subtract(displayPosition).toVector3f();
                    Vector3f p2    = eye.clone().add(p.getEyeLocation().getDirection()).subtract(displayPosition).toVector3f();
                    return new PointEntry(p, p1, p2);

                })
                .collect(Collectors.toList());
    }

    /** Convenience ctor: 0‥1 for both axes */
    public PlanePointDetector(Collection<? extends Player> players, Vector displayPosition) {
        this(players, displayPosition, new Range(0f, 1f), new Range(0f, 1f));
    }

    /** Returns players whose view ray intersects the rectangle defined by {@code planeTransform}. */
    public List<Player> lookingAt(Matrix4f planeTransform) {

        return points.stream()
                .filter(e -> intersects(e.p1, e.p2, planeTransform, xRange, yRange))
                .map(e -> e.player)
                .collect(Collectors.toList());
    }


    /* ────────────────────────────────────────────────────────────────────────── */
    /*  Implementation details (private)                                         */
    /* ────────────────────────────────────────────────────────────────────────── */

    private final List<PointEntry> points;
    private final Range xRange, yRange;
    private final Vector displayPosition;

    private static boolean intersects(Vector3f p1, Vector3f p2,
                                      Matrix4f plane, Range xR, Range yR) {

        Matrix4f inv = new Matrix4f(plane).invert();
        Vector3f t1  = transform(inv, p1);
        Vector3f t2  = transform(inv, p2);

        Vector3f hit = lineAtZ(t1, t2, 0f);        // where the ray crosses z=0 in plane-space
        return xR.contains(hit.x) && yR.contains(hit.y);
    }

    private static Vector3f lineAtZ(Vector3f a, Vector3f b, float z) {
        float t = (z - a.z) / (b.z - a.z);
        return new Vector3f(a).lerp(b, t);
    }

    private static Vector3f transform(Matrix4f m, Vector3f p) {
        Vector4f h = new Vector4f(p, 1f);
        m.transform(h);
        if (h.w != 0f) h.div(h.w);            // perspective divide
        return new Vector3f(h.x, h.y, h.z);
    }

//    private static Vector3f toVec3(Vector v) {
//        return new Vector3f((float) v.getX(), (float) v.getY(), (float) v.getZ());
//    }

    /* Simple float range helper */
    public record Range(float min, float max) {
        public boolean contains(float f) { return f >= min && f <= max; }
    }

    /* Tuple of (player, rayStart, rayEnd) */
    private record PointEntry(Player player, Vector3f p1, Vector3f p2) {}
}