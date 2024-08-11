package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public final class SpawnedDisplayAnimation {
    String animationTag;
    ArrayList<SpawnedDisplayAnimationFrame> frames = new ArrayList<>();
    String partTag = null;
    boolean respectGroupScale = false;

    public SpawnedDisplayAnimation(){}

    public SpawnedDisplayAnimation(String partTag){
        this.partTag = DisplayEntityPlugin.partTagPrefix+partTag;
    }

    SpawnedDisplayAnimation(SpawnedDisplayEntityGroup group){
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 0);
        for (SpawnedDisplayEntityPart part : group.spawnedParts){
            if (part.isMaster()){
                continue;
            }
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                frame.setInteractionTranslation(part, DisplayUtils.getInteractionTranslation((Interaction) part.getEntity()));
            }
            else{
                frame.setDisplayEntityTransformation(part, ((Display) part.getEntity()).getTransformation());
            }
        }
        addFrame(frame);
    }

    SpawnedDisplayAnimation(SpawnedDisplayEntityGroup group, String partTag){
        this.partTag = partTag;
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, 0);
        for (SpawnedDisplayEntityPart part : group.spawnedParts){
            if (part.isMaster()){
                continue;
            }
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                frame.setInteractionTranslation(part, DisplayUtils.getInteractionTranslation((Interaction) part.getEntity()));
            }
            else{
                frame.setDisplayEntityTransformation(part, ((Display) part.getEntity()).getTransformation());
            }
        }
        addFrame(frame);
    }


    public String getAnimationTag() {
        return animationTag;
    }

    public void setAnimationTag(String animationTag) {
        this.animationTag = animationTag;
    }

    public boolean isPartAnimation(){
        return partTag != null;
    }

    public String getCleanPartTag() {
        return partTag.replace(DisplayEntityPlugin.partTagPrefix, "");
    }

    public String getPartTag() {
        return partTag;
    }

    public ArrayList<SpawnedDisplayAnimationFrame> getFrames() {
        return new ArrayList<>(frames);
    }

    public void setFrames(ArrayList<SpawnedDisplayAnimationFrame> frames) {
        this.frames = frames;
    }

    /**
     * Add a frame to a SpawnedDisplayAnimation
     * @param frame the frame to add
     * @return An empty frame if the passed in frame is identical to the frame before it, else the passed in frame
     */
    public SpawnedDisplayAnimationFrame addFrame(SpawnedDisplayAnimationFrame frame){
        if (!frames.isEmpty() && frames.get(frames.size()-1).equals(frame)){
            SpawnedDisplayAnimationFrame newFrame = new SpawnedDisplayAnimationFrame(frame.delay, frame.duration);
            frames.add(newFrame);
            return newFrame;
        }
        frames.add(frame);
        return frame;
    }

    /**
     * Add a frame to a SpawnedDisplayAnimation regardless of if it's identical to the frame before it
     * @param frame the frame to add
     */
    public void forceAddFrame(SpawnedDisplayAnimationFrame frame){
        frames.add(frame);
    }



    public void removeFrame(SpawnedDisplayAnimationFrame frame){
        frames.remove(frame);
    }

    public void groupScaleRespect(boolean respect){
        respectGroupScale = respect;
    }

    public boolean groupScaleRespect(){
        return respectGroupScale;
    }


    public void remove(){
        for (SpawnedDisplayAnimationFrame frame : frames){
            frame.displayTransformations.clear();
            frame.interactionTranslations.clear();
        }
        frames.clear();
    }

    public DisplayAnimation toDisplayAnimation(){
        DisplayAnimation anim = new DisplayAnimation();

        anim.animationTag = this.animationTag;
        anim.partTag = this.partTag;
        anim.respectGroupScale = this.respectGroupScale;
        for (SpawnedDisplayAnimationFrame frame : frames){
            anim.addFrame(frame.toDisplayAnimationFrame());
        }
        return anim;
    }

    /** Reverses the order of all frames in this SpawnedDisplayAnimation
     */
    public void reverse(){
        Collections.reverse(frames);
    }

    /** Creates a SpawnedDisplayAnimation with the same frames as
     * this one in a reversed order
     * @return The reversed SpawnedDisplayAnimation
     */
    public SpawnedDisplayAnimation getReversedAnimation(){
        SpawnedDisplayAnimation reversed = new SpawnedDisplayAnimation();
        for (int i = frames.size()-1; i < 0; i--){
            SpawnedDisplayAnimationFrame frame = frames.get(i);
            SpawnedDisplayAnimationFrame newFrame = new SpawnedDisplayAnimationFrame(frame.delay, frame.duration);
            newFrame.displayTransformations = new HashMap<>(frame.displayTransformations);
            newFrame.interactionTranslations = new HashMap<>(frame.interactionTranslations);
            reversed.addFrame(newFrame);
        }
        return reversed;
    }

}
