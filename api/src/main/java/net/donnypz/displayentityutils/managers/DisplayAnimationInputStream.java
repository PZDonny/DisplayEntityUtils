package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.utils.DisplayEntities.saved.OldSound;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

class DisplayAnimationInputStream extends DisplayObjectInputStream{
    DisplayAnimationInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass desc = super.readClassDescriptor();
        if (desc.getName().equals("org.bukkit.Sound")){ //Because of old sound HashMaps using an enum
            //if (VersionUtils.is_1_21_2){
            return ObjectStreamClass.lookup(OldSound.class);
            //}
        }
        return desc;
    }
}
