package net.donnypz.displayentityutils.utils.DisplayEntities;

import java.io.*;
import java.util.ArrayList;

public final class DisplayAnimation implements Serializable {
    String animationTag;
    ArrayList<DisplayAnimationFrame> frames = new ArrayList<>();
    String partTag;
    boolean respectGroupScale = false;

    DisplayAnimation(){}

    @Serial
    private static final long serialVersionUID = 99L;
    public static final String fileExtension = ".deanim";


    public String getAnimationTag() {
        return animationTag;
    }

    public String getPartTag() {
        return partTag;
    }

    public boolean isPartAnimation(){
        return partTag != null;
    }

    public ArrayList<DisplayAnimationFrame> getFrames() {
        return new ArrayList<>(frames);
    }

    public void setFrames(ArrayList<DisplayAnimationFrame> frames) {
        this.frames = frames;
    }

    public void addFrame(DisplayAnimationFrame frame){
        frames.add(frame);
    }

    public void removeFrame(DisplayAnimationFrame frame){
        frames.remove(frame);
    }

    public SpawnedDisplayAnimation toSpawnedDisplayAnimation(){
        SpawnedDisplayAnimation anim = new SpawnedDisplayAnimation();
        anim.animationTag = this.animationTag;
        anim.respectGroupScale = this.respectGroupScale;
        anim.partTag = this.partTag;
        for (DisplayAnimationFrame frame : frames){
            anim.addFrame(frame.toSpawnedDisplayAnimationFrame());
        }
        return anim;
    }
}
