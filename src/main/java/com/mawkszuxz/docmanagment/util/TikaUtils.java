package com.mawkszuxz.docmanagment.util;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;

public class TikaUtils {
    private static final Tika tika = new Tika();

    public static String extractText(InputStream stream) throws IOException, TikaException {
        return tika.parseToString(stream);
    }

    public static String detectTitle(InputStream stream) throws IOException, TikaException {
        Metadata metadata = new Metadata();
        tika.parse(stream, metadata);
        return metadata.get("title");
    }
}

