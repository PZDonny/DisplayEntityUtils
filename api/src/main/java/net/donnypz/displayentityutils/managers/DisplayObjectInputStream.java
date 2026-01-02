package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.utils.DisplayEntities.DEUSound;
import net.donnypz.displayentityutils.utils.DisplayEntities.saved.OldSound;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class DisplayObjectInputStream extends ObjectInputStream {

    private static final String OLD_PACKAGE = "com.pzdonny";
    private static final String NEW_PACKAGE = "net.donnypz";
    private static final String BUKKIT_SOUND_ENUM = "org.bukkit.Sound";
    private static final String ANIMATION_SOUND = "AnimationSound";
    DisplayObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {

    //Convert Old Serialized Objects with new package name
        String name = desc.getName();
        if (name.startsWith(OLD_PACKAGE)) {
            name = NEW_PACKAGE + name.substring(OLD_PACKAGE.length());
        }
        return Class.forName(name);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass desc = super.readClassDescriptor();
        String name = desc.getName();
        if (name.equals(BUKKIT_SOUND_ENUM)){ //Because of old sound HashMaps using an enum
            return ObjectStreamClass.lookup(OldSound.class);
        }
        if (name.endsWith(ANIMATION_SOUND)){
            return ObjectStreamClass.lookup(DEUSound.class);
        }
        return desc;
    }
}
