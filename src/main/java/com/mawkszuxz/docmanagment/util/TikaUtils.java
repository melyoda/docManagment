package com.mawkszuxz.docmanagment.util;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public class TikaUtils {
    private static final Tika tika = new Tika();

    public static DocumentData extractData(InputStream stream) throws TikaException, IOException, SAXException {
        try {

            Metadata metadata = new Metadata();
//           BodyContentHandler handler = new BodyContentHandler(-1);
            BodyContentHandler handler = new BodyContentHandler(50 * 1024 * 1024);
            AutoDetectParser parser = new AutoDetectParser();

            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "filename.ext");

            parser.parse(stream, handler, metadata);

            String title = metadata.get("title");
            String content = handler.toString();

            if (title == null || title.isEmpty()) {
                title = extractTitleFromContent(content);
            }

            return new DocumentData(title, content, metadata);
        }catch (Exception e){
            throw new TikaException("Failed to parse document: " + e.getMessage(), e);
        }
    }

    //--->fallback if no title is in metadata<---
    private static String extractTitleFromContent(String content) {

        if (content == null || content.isEmpty()) return "Untitled";
        String firstLine = content.split("\n")[0];
        if (firstLine.length() > 100) {
            return firstLine.substring(0, 100) + "...";
        }
        return firstLine;
    }
    public record DocumentData(String title, String content, Metadata metadata) { }

}


