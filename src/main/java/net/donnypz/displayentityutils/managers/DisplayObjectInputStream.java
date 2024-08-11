package net.donnypz.displayentityutils.managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

final class DisplayObjectInputStream extends ObjectInputStream {
    DisplayObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {

    //Convert Old Serialized Objects with new package name
        String name = desc.getName();
        if (name.startsWith("com.pzdonny")) {
            name = "net.donnypz" + name.substring("com.pzdonny".length());
        }

        return Class.forName(name);
    }
}
