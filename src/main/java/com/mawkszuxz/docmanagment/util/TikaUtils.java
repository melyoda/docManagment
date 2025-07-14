package com.mawkszuxz.docmanagment.util;

//import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public class TikaUtils {
    //private static final Tika tika = new Tika();

    /**
     * Extracts Data from pdf and docs
     *
     * @param stream InputStream
     * @param originalFilename String the original filename
     *
     * @return DocumentData record of the extracted stuff (String title, String content, Metadata metadata)
     * */
    public static DocumentData extractData(InputStream stream, String originalFilename) throws TikaException, IOException, SAXException {
        try {
            Metadata metadata = new Metadata();
            BodyContentHandler handler = new BodyContentHandler(100 * 1024 * 1024);
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();

            //providing the original filename to tika
            if(originalFilename != null && !originalFilename.isEmpty()) {
                metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, originalFilename);
                System.out.println("[TikaUtils] Set RESOURCE_NAME_KEY to: " + originalFilename);
            }else {
                metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "unknown_document");
                System.out.println("[TikaUtils] RESOURCE_NAME_KEY set to unknown_document as original filename was missing");
            }

            System.out.println("[TikaUtils] Starting Tika parsing for: " + (originalFilename != null ? "unknown_document" : "N/A"));
            parser.parse(stream, handler, metadata, context);
            System.out.println("[TikaUtils] Tika parsing completed.");

            //logging all extracted metadata keys and their values
            System.out.println("[TikaUtils] ---- Extracted Metadata ----");
            for (String name : metadata.names()) {
                System.out.println(" key: '" + name + "' value: '" + metadata.get(name) + "'");
            }
            System.out.println("[TikaUtils] ----------------------------");

            //attempting to get title using various metadata fields
            String title = metadata.get(TikaCoreProperties.TITLE);
            if (title != null && !title.isEmpty()) {
                System.out.println("[TikaUtils] Title found using TikaCoreProperties.TITLE: '" + title + "'");
            }else {
                title = metadata.get(DublinCore.TITLE);
                if (title != null && !title.isEmpty()) {
                    System.out.println("[TikaUtils] Title found using DublinCore.TITLE: '" + title + "'");
                }else{
                    title = metadata.get("title");//generic title key
                    if (title != null && !title.isEmpty()) {
                        System.out.println("[TikaUtils] Title found using generic 'title' key: '" + title + "'");
                    }else{
                        System.out.println("[TikaUtils] No explicit title found in metadata");
                    }
                }
            }

            String content = handler.toString();
            System.out.println("[TikaUtils] Extracted content length: "+ (content != null ? content.length() : "null"));
            if(content != null && !content.isEmpty()) {
                //log a preview of the content
                //TODO : remove later or put something else
                System.out.println("[TikaUtils] Content preview (first 200 chars): " +
                        content.substring(0, Math.min(content.length(), 200)).replace("\n", " "));
            }else {
                System.out.println("[TikaUtils] WARNING: Extracted content is null or empty!");
            }

            // Heuristic: Check if content looks like a class@hashcode string
            if (content != null && content.length() < 100 && content.contains("@") && !content.contains(" ")) {
                System.err.println("[TikaUtils] CRITICAL WARNING: Extracted content '" + content + "' might be an object's toString() representation, not actual document text!");
            }

            // Fallback for title if not found in metadata
            if (title == null || title.trim().isEmpty()) {
                System.out.println("[TikaUtils] Applying fallback to extract title from content or use filename.");
                title = extractTitleFromContentOrFilename(content, originalFilename);
            }

            return new DocumentData(title, content, metadata);
        }catch (Exception e) { // Catch generic Exception to ensure logging
            System.err.println("[TikaUtils] Exception during Tika processing for '" + (originalFilename != null ? originalFilename : "N/A") + "': " + e.getMessage());
            // e.printStackTrace(); // Uncomment for full stack trace during development
            throw new TikaException("Failed to parse document '" + (originalFilename != null ? originalFilename : "N/A") + "': " + e.getMessage(), e);
        }
    }

    //--->fallback if no title is in metadata<---
    private static String extractTitleFromContentOrFilename(String content, String originalFilename) {
        String fallbackTitle = (originalFilename != null && !originalFilename.isEmpty()) ? originalFilename : "Untitled Document";

        if (content == null || content.trim().isEmpty()) {
            System.out.println("[TikaUtils Fallback] Content is empty. Using filename as title: '" + fallbackTitle + "'");
            return fallbackTitle;
        }

        String[] lines = content.split("\\r?\\n"); // Split by newline, handling Windows and Unix endings
        String firstMeaningfulLine = "";
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                firstMeaningfulLine = line.trim();
                break;
            }
        }

        if (!firstMeaningfulLine.isEmpty()) {
            if (firstMeaningfulLine.length() > 150) {
                fallbackTitle = firstMeaningfulLine.substring(0, 150) + "...";
                System.out.println("[TikaUtils Fallback] Extracted title from content (truncated): '" + fallbackTitle + "'");
            } else {
                fallbackTitle = firstMeaningfulLine;
                System.out.println("[TikaUtils Fallback] Extracted title from content: '" + fallbackTitle + "'");
            }
        } else {
            System.out.println("[TikaUtils Fallback] No meaningful first line in content. Using filename as title: '" + fallbackTitle + "'");
        }
        return fallbackTitle;
    }
    //using record for DocumentData
    public record DocumentData(String title, String content, Metadata metadata) {}

}


