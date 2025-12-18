package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.utils.DisplayEntities.DEUSound;
import net.donnypz.displayentityutils.utils.DisplayEntities.saved.OldSound;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

class DisplayAnimationInputStream extends DisplayObjectInputStream{

    private static final String ANIMATION_SOUND = "AnimationSound";

    DisplayAnimationInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass desc = super.readClassDescriptor();
        String name = desc.getName();
        if (name.equals("org.bukkit.Sound")){ //Because of old sound HashMaps using an enum
            //if (VersionUtils.is_1_21_2){
            return ObjectStreamClass.lookup(OldSound.class);
            //}
        }
        if (name.endsWith(ANIMATION_SOUND)){
            return ObjectStreamClass.lookup(DEUSound.class);
        }
        return desc;
    }
}
