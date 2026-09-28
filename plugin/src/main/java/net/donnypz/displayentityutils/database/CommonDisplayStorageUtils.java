package net.donnypz.displayentityutils.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

class CommonDisplayStorageUtils {

    static ByteArrayInputStream toByteArrayInputStream(Object entityObject) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
        ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
        objOut.writeObject(entityObject);
        gzipOut.close();
        objOut.close();

        byte[] data = byteOut.toByteArray();
        byteOut.close();
        return new ByteArrayInputStream(data);
    }

    static byte[] toByteArray(Object entityObject) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
        ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
        objOut.writeObject(entityObject);
        gzipOut.close();
        objOut.close();

        byte[] data = byteOut.toByteArray();
        byteOut.close();
        return data;
    }
}
