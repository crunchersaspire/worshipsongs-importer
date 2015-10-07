package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;

public class SongParser
{
    String lyrics, verseOrder;

    public String parseTitle(String input)
    {
        String title = parseAttribute(input, "title");
        if(title == "") {
            throw new NullPointerException("Title should not be empty");
        }
        return title;
    }

    public String parseAuthor(String input)
    {
        return parseAttribute(input, "author");
    }

    public String parseAlternateTitle(String input)
    {
        return parseAttribute(input, "alternateTitle");
    }

    public String parseSearchTitle(String title, String alternateTitle)
    {
        return (title + "@" + alternateTitle).toLowerCase();
    }

    public String parseSearchLyrics(String lyrics)
    {
        return lyrics.toLowerCase();
    }

    public String parseVerseOrder(String input)
    {
        return parseAttribute(input, "verseOrder");
    }

    public String parseLyrics(String lyrics)
    {
        return lyrics.split(".*=")[0].trim();
    }

    public String parseAttribute(String input, String attributeName)
    {
        if (!input.isEmpty()) {
            String attribute = findMatchingData(input, attributeName);
            if (attribute.contains("=")) {
                return attribute.split("=")[1];
            }
        }
        return "";
    }

    public String findMatchingData(String input, String attributeName)
    {
        Pattern pattern = Pattern.compile(attributeName + "=.*");
        Matcher matcher = pattern.matcher(input);
        String matchingData = "";

        while (matcher.find()) {
            matchingData = matcher.group(0);
        }
        return matchingData;
    }

    public String getXmlLyrics(String lyrics, String verseOrder)
    {
        this.lyrics = lyrics;
        this.verseOrder = verseOrder;

        Writer out = new StringWriter();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            getSongTag(document);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            document.setXmlStandalone(true);
            transformer.transform(new DOMSource(document), new StreamResult(out));

            System.out.print("Data:"+out.toString());
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return out.toString();
    }

    public void getSongTag(Document document)
    {
        Element song = document.createElement("song");
        Attr version = document.createAttribute("version");
        version.setValue("1.0");
        song.setAttributeNode(version);
        document.appendChild(song);

        getLyricsTag(song, document);
    }

    public void getLyricsTag(Element song, Document document)
    {
        Element lyrics = document.createElement("lyrics");
        song.appendChild(lyrics);

        getVerseTag(lyrics, document);
    }

    public void getVerseTag(Element lyrics, Document document)
    {
        Element verse = document.createElement("verse");

        Attr type = document.createAttribute("type");
        type.setValue("1.0");
        verse.setAttributeNode(type);

        Attr label = document.createAttribute("label");
        label.setValue("1.0");
        verse.setAttributeNode(label);

        verse.appendChild(document.createCDATASection("data"));
        lyrics.appendChild(verse);
    }

    public String[] parseVerses(String verseOrder)
    {
        return verseOrder.split(" ");
    }

    public String[] parseTypeLabel(String verse)
    {
        return verse.split("");
    }
}
