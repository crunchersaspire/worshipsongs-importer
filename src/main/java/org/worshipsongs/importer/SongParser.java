package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SongParser
{
    final static Logger logger = Logger.getLogger(SongParser.class.getName());

    String parseTitle(String input)
    {
        String title = parseAttribute(input, "title");
        if(title == "") {
            throw new NullPointerException("Title should not be empty");
        }
        return title;
    }

    String parseAuthor(String input)
    {
        return parseAttribute(input, "author");
    }

    String parseAlternateTitle(String input)
    {
        return parseAttribute(input, "alternateTitle");
    }

    String parseSearchTitle(String title, String alternateTitle)
    {
        return (title + "@" + alternateTitle).toLowerCase();
    }

    String parseSearchLyrics(String lyrics)
    {
        return lyrics.toLowerCase();
    }

    String parseVerseOrder(String input)
    {
        return parseAttribute(input, "verseOrder");
    }

    String parseLyrics(String lyrics)
    {
        return lyrics.split(".*=")[0].trim();
    }

    String parseAttribute(String input, String attributeName)
    {
        if (!input.isEmpty()) {
            String attribute = findMatchingData(input, attributeName);
            if (attribute.contains("=")) {
                return attribute.split("=")[1];
            }
        }
        return "";
    }

    String findMatchingData(String input, String attributeName)
    {
        Pattern pattern = Pattern.compile(attributeName + "=.*");
        Matcher matcher = pattern.matcher(input);
        String matchingData = "";
        while (matcher.find()) {
            matchingData = matcher.group(0);
        }
        return matchingData;
    }

    String getXmlLyrics(String lyrics, String verseOrder)
    {
        //String verseOrders[] = parseVerseOrders(verseOrder);
        Writer out = new StringWriter();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            document.setXmlStandalone(true);

            Element elementSong = getSongTag(document);
            document.appendChild(elementSong);

            Element elementLyrics = getLyricsTag(document);
            elementSong.appendChild(elementLyrics);

            Element elementVerse = getVerseTag(document);
            elementLyrics.appendChild(elementVerse);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(out));

        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "Exception occur", e);
        } catch (TransformerException e) {
            logger.log(Level.SEVERE, "Exception occur", e);
        }
        return out.toString();
    }

    Element getSongTag(Document document)
    {
        Element song = document.createElement("song");
        Attr version = document.createAttribute("version");
        version.setValue("1.0");
        song.setAttributeNode(version);
        return song;
    }

    Element getLyricsTag(Document document)
    {
        Element lyrics = document.createElement("lyrics");
        return lyrics;
    }

    Element getVerseTag(Document document)
    {
        Element verse = document.createElement("verse");
        Attr type = document.createAttribute("type");
        type.setValue("");
        verse.setAttributeNode(type);
        Attr label = document.createAttribute("label");
        label.setValue("");
        verse.setAttributeNode(label);
        verse.appendChild(document.createCDATASection("data"));
        return verse;
    }

    String[] parseVerseOrders(String verseOrder)
    {
        return verseOrder.split(" ");
    }

    String parseVerseType(String verse)
    {
        return verse.split("")[1];
    }

    String parseVerseLabel(String verse)
    {
        return verse.split("")[2];
    }
}
