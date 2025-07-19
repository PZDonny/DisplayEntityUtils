package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasElement;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TextDisplayRippleCanvasElement extends TextDisplayCanvasElement {
    private int lifeTime = 400;
    private int updateInterval = 10;
    private int age = 0;
    private int ringSize = 1;
    private Color color = Color.fromARGB(255, 0, 0, 255);

    public TextDisplayRippleCanvasElement(Color color, int ringSize, int x, int y) {
        this(ringSize, x, y);
        this.color = color;
    }

    public TextDisplayRippleCanvasElement(List<Integer> color, int ringSize, int x, int y) {
        this(ringSize, x, y);
        this.color = Color.fromARGB(color.getFirst(), color.get(1), color.get(2), color.getLast());
    }

    public TextDisplayRippleCanvasElement(int ringSize, int x, int y) {
        needsUpdates = true;
        this.x = x;
        this.y = y;
        this.ringSize = normalizeRingSize(ringSize);
        makeRing();
    }

    @Override
    public void update() {

        pixels.forEach(new Consumer<TextDisplayElementPixel>() {
            @Override
            public void accept(TextDisplayElementPixel pixel) {
               pixel.setColor(color);
            }
        });



        if (!needsUpdates){
            return;
        }
        age++;
        if (age%updateInterval!=0){
            return;
        }
        if (age>=lifeTime){
            isDead = true;
            needsUpdates = false;
            return;
        }
        if (ringSize==1){
            ringSize = 4;
        }else {
            ringSize += 4; // ensure it stays %4==0
        }
        pixels = new Matrix2dContainer<>(TextDisplayElementPixel.class);
        makeRing();
    }

    @Override
    public void remove() {
        // If needed: maybe clear `pixels.clear()` here or notify something?
    }

    public int getRingSize() {
        return ringSize;
    }

    private int normalizeRingSize(int input) {
        if (input < 1) return 1;
        if (input != 1 && input % 4 != 0) {
            input += 4 - (input % 4); // cleaner than a while loop
        }
        return input;
    }

    private void makeRing() {
        List<List<Integer>> ringCords = makeRingCords(ringSize);
        for (List<Integer> cords : ringCords) {
            int pixelX = cords.getFirst();
            int pixelY = cords.getLast();
            new TextDisplayElementPixel(color, x + pixelX, y + pixelY, pixels);
        }
    }

    public static List<List<Integer>> makeRingCords(int pixelCount) {
        if (pixelCount <= 0) pixelCount = 1;

        double estRadius = pixelCount / (2.0 * Math.PI);
        int radius = Math.max(1, (int) Math.round(estRadius));

        Set<List<Integer>> unique = new LinkedHashSet<>();

        while (true) {
            unique.clear();
            double step = 2.0 * Math.PI / pixelCount;
            for (int i = 0; i < pixelCount; i++) {
                double theta = i * step;
                int x = (int) Math.round(radius * Math.cos(theta));
                int y = (int) Math.round(radius * Math.sin(theta));
                unique.add(List.of(x, y));
            }
            if (unique.size() >= pixelCount) break;
            radius++;

        }

        List<List<Integer>> cords = new ArrayList<>(unique);
        if (cords.size() > pixelCount) {
            double skip = (double) cords.size() / pixelCount;
            List<List<Integer>> trimmed = new ArrayList<>(pixelCount);
            for (int i = 0; i < pixelCount; i++) {
                trimmed.add(cords.get((int) Math.floor(i * skip)));
            }
            cords = trimmed;
        }
        return cords;
    }
}
