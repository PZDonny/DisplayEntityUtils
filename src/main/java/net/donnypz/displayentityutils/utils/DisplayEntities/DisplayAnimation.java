package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.util.ArrayList;

public final class DisplayAnimation implements Serializable {
    String animationTag;
    ArrayList<DisplayAnimationFrame> frames = new ArrayList<>();
    String partTag;
    boolean respectGroupScale = true;
    PartFilter filter;

    DisplayAnimation(){}

    @Serial
    private static final long serialVersionUID = 99L;
    public static final String fileExtension = ".deanim";


    public String getAnimationTag() {
        return animationTag;
    }

    /**
     * Get the part tag applied to this animation, before the addition of part filters
     * @return a string or null
     */
    public String getPartTag() {
        return partTag;
    }

    /**
     * Get the filter that will be used when animating
     * @return
     */
    public PartFilter getPartFilter(){
        return filter;
    }

    public boolean isFilteredAnimation(){
        return partTag != null;
    }

    public ArrayList<DisplayAnimationFrame> getFrames() {
        return new ArrayList<>(frames);
    }

    public void setFrames(ArrayList<DisplayAnimationFrame> frames) {
        this.frames = frames;
    }

    public void setPartFilter(PartFilter filter){
        this.filter = filter;
    }

    public void addFrame(DisplayAnimationFrame frame){
        frames.add(frame);
    }

    public void removeFrame(DisplayAnimationFrame frame){
        frames.remove(frame);
    }

    @ApiStatus.Internal
    public void adaptOldSounds(){
        for (DisplayAnimationFrame frame: frames){
            frame.repairOldSounds();
        }
    }

    public SpawnedDisplayAnimation toSpawnedDisplayAnimation(){
        SpawnedDisplayAnimation anim = new SpawnedDisplayAnimation(filter);
        anim.animationTag = this.animationTag;
        anim.respectGroupScale = this.respectGroupScale;

        if (this.filter != null){
            anim.filter = this.filter.clone();
            if (partTag != null) anim.filter.includePartTag(LegacyUtils.stripLegacyPartTagPrefix(partTag));
        }
        else if (partTag != null){
            anim.filter = new PartFilter().includePartTag(LegacyUtils.stripLegacyPartTagPrefix(partTag));
        }

        for (DisplayAnimationFrame frame : frames){
            anim.forceAddFrame(frame.toSpawnedDisplayAnimationFrame());
        }
        return anim;
    }
}
