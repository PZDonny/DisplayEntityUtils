package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.utils.OldSound;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

public class DisplayAnimationInputStream extends DisplayObjectInputStream{
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

    /*@Override
    public Object readObjectOverride() throws IOException {
        try {
            return super.readObjectOverride();
        } catch (InvalidClassException | ClassNotFoundException | InvalidObjectException e) {
            System.out.println("Skip: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Object readUnshared() throws IOException {
        try {
            Object obj = super.readUnshared();
            if (obj instanceof Enum e) {
                System.out.println(e.name());
                //return handleEnum((Enum<?>) obj);
            }
            return obj;
        }
        catch (InvalidClassException | ClassNotFoundException e) {
            System.out.println("Skip " + e.getMessage());
            return null;
        }
    }*/
}
