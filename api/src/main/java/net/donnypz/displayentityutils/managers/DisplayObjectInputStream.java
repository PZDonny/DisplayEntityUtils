package net.donnypz.displayentityutils.managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class DisplayObjectInputStream extends ObjectInputStream {

    private static final String OLD_PACKAGE = "com.pzdonny";
    DisplayObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {

    //Convert Old Serialized Objects with new package name
        String name = desc.getName();
        if (name.startsWith(OLD_PACKAGE)) {
            name = "net.donnypz" + name.substring(OLD_PACKAGE.length());
        }
        return Class.forName(name);
    }
}
