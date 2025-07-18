package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextDisplayGifCanvasElement extends TextDisplayCanvasElement {
        private int frame = 0;
        private List<Matrix2dContainer<TextDisplayElementPixel>> frames = new ArrayList<>();
        private int age = 0;
    public TextDisplayGifCanvasElement(){
        updateInterval = 80;
    }
    private void frameUp(){
        frame++;
        if (frame>frames.size()-1){
            frame = 0;
        }
    }

        @Override
        public void update() {
            age++;
            if (age%updateInterval!=0){
                return;
            }
            pixels = new Matrix2dContainer<>(TextDisplayElementPixel.class);
            Matrix2dContainer<TextDisplayElementPixel> thisFrame = frames.get(frame);
            //frameUp();
            thisFrame.forEach(new Consumer<TextDisplayElementPixel>() {
                @Override
                public void accept(TextDisplayElementPixel pixel) {
                    TextDisplayElementPixel clone = new TextDisplayElementPixel( List.of(pixel.getAlpha(),pixel.getRed(), pixel.getGreen() ,pixel.getBlue()),pixel.getX(),pixel.getY(), pixels);
                    clone.setPixelHeight(pixel.getPixelHeight());
                    clone.setPixelWidth(pixel.getPixelWidth());
                    clone.setTwoFaced(pixel.isTwoFaced());
                }
            });
        }

        @Override
    public void remove() {
        pixels = new Matrix2dContainer<>(TextDisplayElementPixel.class);
        frames.clear();
    }

    public List<Matrix2dContainer<TextDisplayElementPixel>> getFrames() {
        return frames;
    }

    public void setFrames(List<Matrix2dContainer<TextDisplayElementPixel>> frames) {
        this.frames = frames;
    }

    public int getFrame() {
        return frame;
    }
    public void setFrame(int frame) {
        this.frame = frame;
    }



}
