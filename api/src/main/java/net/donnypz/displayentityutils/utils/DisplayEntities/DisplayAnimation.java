package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.LegacyUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public final class DisplayAnimation implements Serializable {
    String animationTag;
    ArrayList<DisplayAnimationFrame> frames = new ArrayList<>();
    String partTag;
    boolean respectGroupScale = true;
    boolean dataChanges = true;
    PartFilter filter;

    @Serial
    private static final long serialVersionUID = 99L;
    public static final String fileExtension = ".deanim";


    DisplayAnimation(){}



    public String getAnimationTag() {
        return animationTag;
    }

    /**
     * Get the part tag applied to this animation, before the addition of part filters
     * @return a string or null
     */
    @Deprecated(since = "2.6.3")
    public @Nullable String getPartTag() {
        return partTag;
    }

    /**
     * Get the filter that will be used when animating
     * @return {@link PartFilter} or null if not set
     */
    public @Nullable PartFilter getPartFilter(){
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

    /**
     * Get if this animation allows for texture changes to block/item displays and text display text
     * @return a boolean
     */
    public boolean allowsTextureChanges(){
        return dataChanges;
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
        anim.dataChanges = this.dataChanges;

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
