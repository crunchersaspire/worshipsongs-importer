package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import com.sun.org.apache.xpath.internal.SourceTree;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class SongParserTest
{
    private SongParser parser = new SongParser();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    String lyrics = "[V1]\n" +
            "Lord I lift Your name on high\n" +
            "Lord I love to sing Your praises\n" +
            "[O1]\n" +
            "I’m so glad You're in my life\n" +
            "I’m so glad You came to save us\n" +
            " \n" +
            "[C1]\n" +
            "You came from heaven to earth \n" +
            "To show the way\n" +
            "[O2]\n" +
            "From the earth to the cross, \n" +
            "My debts to pay\n" +
            "[O3]\n" +
            "From the cross to the grave, \n" +
            "From the grave to the sky\n" +
            "Lord I lift Your name on high";

    String searchLyrics = lyrics.toLowerCase();
    @Test
    public void testParseTitle1()
    {
        String input = "title=foo";
        String expected = "foo";
        String result = parser.parseTitle(input);
        assertEquals(expected, result);
    }

    @Test
    public void testParseTitle2()
    {
        String input = "title:foo";
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Title should not be empty");
        parser.parseTitle(input);
    }

    @Test
    public void testParseTitle3()
    {
        String input = "barbarbar\n"+
                "title=foo\n" +
                "foofoo";
        String expected = "foo";
        String result = parser.parseTitle(input);
        assertEquals(expected, result);
    }

    @Test
    public void testParseAuthor()
    {
        assertEquals("Foo", parser.parseAuthor("author=Foo"));
        assertEquals("", parser.parseAuthor("author:Foo"));
        assertEquals("Foo", parser.parseAuthor("barbarbar\n"+
                "author=Foo\n" +
                "foofoo"));
    }

    @Test
    public void testParseAlternateTitle()
    {
        assertEquals("", parser.parseAlternateTitle(""));
        assertEquals("Foo bar", parser.parseAlternateTitle("alternateTitle=Foo bar"));
        assertEquals("", parser.parseAlternateTitle("alternateTitle:Foo bar"));
        assertEquals("Foo bar", parser.parseAlternateTitle("barbarbar\n" +
                "alternateTitle=Foo bar\n" +
                "foofoo"));
    }
    @Test
    public void testParseSearchTitle()
    {
        assertEquals("foo@bar", parser.parseSearchTitle("Foo", "Bar"));
        assertEquals("foo@", parser.parseSearchTitle("Foo", ""));
        assertEquals("@", parser.parseSearchTitle("", ""));
    }

    @Test
    public void testParseVerseOrder()
    {
        assertEquals("", parser.parseVerseOrder(""));
        assertEquals("V1 C1 O1 V2 V3", parser.parseVerseOrder("verseOrder=V1 C1 O1 V2 V3"));
        assertEquals("", parser.parseVerseOrder("verseOrder:V1 C1 O1 V2 V3"));
        assertEquals("V1 C1 O1 V2 V3", parser.parseVerseOrder("barbarbar\n" +
                "verseOrder=V1 C1 O1 V2 V3\n" +
                "foofoo"));
    }

    @Test
    public void testParseLyrics()
    {
        assertEquals(lyrics, parser.parseLyrics(lyrics +
                "\ntitle="));
        assertEquals(lyrics, parser.parseLyrics(lyrics +
                "\nauthor="));
        assertEquals(lyrics, parser.parseLyrics(lyrics));
        assertEquals("", parser.parseLyrics(""));
    }

    @Test
    public void testParseSearchLyrics()
    {
        assertEquals("", parser.parseSearchLyrics(""));
        assertEquals(searchLyrics, parser.parseSearchLyrics(lyrics));
    }

    @Test
    public void testGetXmlLyrics()
    {
        StringBuilder expectedLyrics = new StringBuilder("");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sample_lyrics.xml").getFile());
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                expectedLyrics.append(line).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(expectedLyrics.toString(), parser.getXmlLyrics(lyrics, "v1 o1 c1 o2 o3"));
    }

    @Test
    public void testGetVerseTag()
    {
        Writer out = new StringWriter();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element verseTag = parser.getVerseTag(document);
            document.appendChild(verseTag);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(new DOMSource(document), new StreamResult(out));
            System.out.println(out.toString());

            assertEquals("<verse type=\"\" label=\"\"><![CDATA[data]]></verse>", out.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetLyricsTag()
    {
        Writer out = new StringWriter();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element lyricsTag = parser.getLyricsTag(document);
            document.appendChild(lyricsTag);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(new DOMSource(document), new StreamResult(out));

            assertEquals("<lyrics/>", out.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSongTag()
    {
        Writer out = new StringWriter();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            document.setXmlStandalone(true);

            Element songTag = parser.getSongTag(document);
            document.appendChild(songTag);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            System.out.println(out.toString());
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><song version=\"1.0\"/>", out.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseVerseOrders()
    {
        assertEquals("v1", parser.parseVerseOrders("v1 v2 v3 v4")[0]);
        assertEquals("v2", parser.parseVerseOrders("v1 v2 v3 v4")[1]);
        assertEquals("v3", parser.parseVerseOrders("v1 v2 v3 v4")[2]);
        assertEquals("v4", parser.parseVerseOrders("v1 v2 v3 v4")[3]);
    }

    @Test
    public void testParseVerseType()
    {
        assertEquals("v", parser.parseVerseType("v1"));
        assertEquals("c", parser.parseVerseType("c1"));
    }

    @Test
    public void testParseVerseLabel()
    {
        assertEquals("1", parser.parseVerseType("v1"));
        assertEquals("2", parser.parseVerseType("v2"));
    }


}