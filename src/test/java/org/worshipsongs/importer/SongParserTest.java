package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

public class SongParserTest
{
    private SongParser parser = new SongParser();
    DocumentBuilderFactory docFactory;
    DocumentBuilder docBuilder;
    Document document;
    Transformer transformer;
    Writer out = new StringWriter();

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

    String lyrics1 =
            "Lord I lift Your name on high\n" +
            "Lord I love to sing Your praises\n" +
            "\n" +
            "I’m so glad You're in my life\n" +
            "I’m so glad You came to save us\n" +
            " \n" +
            "\n" +
            "You came from heaven to earth\n" +
            "To show the way\n" +
            "\n" +
            "From the earth to the cross,\n" +
            "My debts to pay\n" +
            "\n" +
            "From the cross to the grave,\n" +
            "From the grave to the sky\n" +
            "Lord I lift Your name on high";

    String searchLyrics = lyrics.toLowerCase();

    @Before
    public void setUp() throws ParserConfigurationException, TransformerConfigurationException
    {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        document = docBuilder.newDocument();
        transformer = TransformerFactory.newInstance().newTransformer();
    }

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
    public void testGetXmlLyrics() throws IOException
    {
        ClassLoader classLoader = getClass().getClassLoader();
        String expectedLyrics = IOUtils.toString(classLoader.getResourceAsStream("lord-i-lift-your-name-on-high.xml"));
        assertEquals(expectedLyrics.toString(), parser.getXmlLyrics(lyrics, "V1 O1 C1 O2 O3"));
    }

    @Test
    public void testGetVerseTag() throws TransformerException
    {
        Element verseTag = parser.getVerseElement(document, "V1");
        document.appendChild(verseTag);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(new DOMSource(document), new StreamResult(out));
        assertEquals("<verse type=\"V\" label=\"1\"><![CDATA[data]]></verse>", out.toString());
    }

    @Test
    public void testGetLyricsTag() throws TransformerException
    {
        Element lyricsTag = parser.getLyricsElement(document);
        document.appendChild(lyricsTag);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(new DOMSource(document), new StreamResult(out));
        assertEquals("<lyrics/>", out.toString());
    }

    @Test
    public void testGetSongTag() throws TransformerException
    {
        document.setXmlStandalone(true);
        Element songTag = parser.getSongElement(document);
        document.appendChild(songTag);
        transformer.transform(new DOMSource(document), new StreamResult(out));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><song version=\"1.0\"/>", out.toString());
    }

    @Test
    public void testParseVerseOrders()
    {
        assertEquals("V1", parser.splitVerseOrder("V1 V2 V3 V4")[0]);
        assertEquals("V2", parser.splitVerseOrder("V1 V2 V3 V4")[1]);
        assertEquals("V3", parser.splitVerseOrder("V1 V2 V3 V4")[2]);
        assertEquals("V4", parser.splitVerseOrder("V1 V2 V3 V4")[3]);
    }

    @Test
    public void testParseVerseType()
    {
        assertEquals("v", parser.splitVerseType("V1"));
        assertEquals("v", parser.splitVerseType("V2"));
        assertEquals("c", parser.splitVerseType("C1"));
        assertEquals("c", parser.splitVerseType("C2"));
        assertEquals("o", parser.splitVerseType("O1"));
        assertEquals("o", parser.splitVerseType("O2"));
    }

    @Test
    public void testParseVerseLabel()
    {
        assertEquals("1", parser.splitVerseLabel("V1"));
        assertEquals("2", parser.splitVerseLabel("V2"));
        assertEquals("1", parser.splitVerseLabel("C1"));
        assertEquals("2", parser.splitVerseLabel("C2"));
        assertEquals("1", parser.splitVerseLabel("O1"));
        assertEquals("2", parser.splitVerseLabel("O2"));
    }
}