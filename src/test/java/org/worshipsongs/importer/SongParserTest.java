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
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SongParserTest
{
    private SongParser parser = new SongParser();
    DocumentBuilderFactory docFactory;
    DocumentBuilder docBuilder;
    Document document;
    Transformer transformer;
    Writer out = new StringWriter();
    ClassLoader classLoader;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    String lyrics = "[V1]\n" +
            "Lord I lift Your name on high\n" +
            "Lord I love to sing Your praises\n" +
            "[O1]\n" +
            "I’m so glad You're in my life\n" +
            "I’m so glad You came to save us\n" +
            "[C1]\n" +
            "You came from heaven to earth\n" +
            "To show the way\n" +
            "[O2]\n" +
            "From the earth to the cross,\n" +
            "My debts to pay\n" +
            "[O3]\n" +
            "From the cross to the grave,\n" +
            "From the grave to the sky\n" +
            "Lord I lift Your name on high";

    String tamilLyrics = "[V1]\n" +
            "இயேசு எனக்கு ஜீவன் தந்தாரே -4\n" +
            "Yesu Enakku Jeevan Thanthaarey – 4\n" +
            "[C1]\n" +
            "துதி பாடல் நான் பாடி\n" +
            "Thuthi Paadal Naan Paadi\n" +
            "இயேசுவையே போற்றி\n" +
            "Yesuvaiye Pottri\n" +
            "[C1]\n" +
            "என்றென்றும் வாழ்த்திடுவோம்\n" +
            "Endrendrum Vaalthiduven\n" +
            "அல்லேலுயா அமென் அல்லேலுயா\n" +
            "Alleluya Amen Alleluya – 2\n" +
            "[V2]\n" +
            "சமாதானம் தந்தார் இயேசு - 4\n" +
            "1.Samathaanam Thanthaar Yesu – 4\n" +
            "[V3]\n" +
            "புது வாழ்வு தந்தார் இயேசு -4\n" +
            "2. Puthu Vaalvu Thanthaar Yesu - 4\n" +
            "[V4]\n" +
            "விடுதலை தந்தார் இயேசு -4\n" +
            "3. Viduthalai Thanthaar Yesu - 4\n" +
            "[V5]\n" +
            "அபிஷேகம் தந்தார் இயேசு - 4\n" +
            "4. Abishegam Thanthaar Yesu – 4";

    String searchLyrics = "lord i lift your name on high lord i love to sing your praises im so glad youre in my life im so glad you came to save us you came from heaven to earth to show the way from the earth to the cross my debts to pay from the cross to the grave from the grave to the sky lord i lift your name on high";

    String xmlLyrics = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><song version=\"1.0\"><lyrics><verse label=\"1\" type=\"v\"><![CDATA[Lord I lift Your name on high\n" +
            "Lord I love to sing Your praises]]></verse><verse label=\"1\" type=\"o\"><![CDATA[I’m so glad You're in my life\n" +
            "I’m so glad You came to save us]]></verse><verse label=\"1\" type=\"c\"><![CDATA[You came from heaven to earth\n" +
            "To show the way]]></verse><verse label=\"2\" type=\"o\"><![CDATA[From the earth to the cross,\n" +
            "My debts to pay]]></verse><verse label=\"3\" type=\"o\"><![CDATA[From the cross to the grave,\n" +
            "From the grave to the sky\n" +
            "Lord I lift Your name on high]]></verse></lyrics></song>";
    @Before
    public void setUp() throws ParserConfigurationException, TransformerConfigurationException
    {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        document = docBuilder.newDocument();
        transformer = TransformerFactory.newInstance().newTransformer();
        classLoader = getClass().getClassLoader();
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
//        assertEquals("", parser.parseSearchLyrics(""));
        assertEquals(searchLyrics, parser.parseSearchLyrics(lyrics));
    }

    @Test
    public void testGetXmlLyrics() throws IOException
    {
        String expectedLyrics = IOUtils.toString(classLoader.getResourceAsStream("lord-i-lift-your-name-on-high.xml"));
        assertEquals(expectedLyrics.toString(), parser.getXmlLyrics(lyrics, "V1 O1 C1 O2 O3"));
    }

    @Test
    public void testGetXmlLyrics1() throws IOException
    {
        String expectedLyrics = IOUtils.toString(classLoader.getResourceAsStream("yesu-enakku-jeevan.xml"));
        assertEquals(expectedLyrics.toString(), parser.getXmlLyrics(tamilLyrics, "V1 C1 C1 V2 V3 V4 V5"));
    }

    @Test
    public void testGetVerseElement() throws TransformerException
    {
        Element verseTag = parser.getVerseElement(document, "V1", "");
        document.appendChild(verseTag);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(new DOMSource(document), new StreamResult(out));
        assertEquals("<verse type=\"v\" label=\"1\"><![CDATA[data]]></verse>", out.toString());
    }

    @Test
    public void testGetLyricsElement() throws TransformerException
    {
        Element lyricsTag = parser.getLyricsElement(document);
        document.appendChild(lyricsTag);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(new DOMSource(document), new StreamResult(out));
        assertEquals("<lyrics/>", out.toString());
    }

    @Test
    public void testGetSongElement() throws TransformerException
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
        assertEquals("", parser.splitVerseType(""));
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
        assertEquals("", parser.splitVerseLabel(""));
    }

    @Test
    public void testParseVerse()
    {
        assertEquals("Lord I lift Your name on high\n" +
                "Lord I love to sing Your praises", parser.splitVerse(lyrics)[1].trim());
        assertEquals("I’m so glad You're in my life\n" +
                "I’m so glad You came to save us", parser.splitVerse(lyrics)[2].trim());
        assertEquals("You came from heaven to earth \n" +
                "To show the way", parser.splitVerse(lyrics)[3].trim());
        assertEquals("From the earth to the cross, \n" +
                "My debts to pay", parser.splitVerse(lyrics)[4].trim());
    }

    @Test
    public void testParseSongBook()
    {
        assertEquals("", parser.parseSongBook(""));
        assertEquals("Foo bar", parser.parseSongBook("songBook=Foo bar"));
        assertEquals("", parser.parseSongBook("songBook:Foo bar"));
        assertEquals("Foo bar", parser.parseSongBook("barbarbar\n" +
                "songBook=Foo bar\n" +
                "foofoo"));
    }

    @Test
    public void testParseSong() throws IOException
    {
        Song song = new Song();
        song.setTitle("Lord I lift Your Name");
        song.setAlternateTitle("Lord I lift Your Name");
        song.setAuthor("Unknown");
        song.setVerseOrder("V1 O1 C1 O2 O3");
        song.setSongBook("");
        song.setLyrics(lyrics);
        song.setXmlLyrics(xmlLyrics);
        song.setSearchTitle((song.getTitle()+"@"+song.getAlternateTitle()).toLowerCase());
        song.setSearchLyrics(searchLyrics);

        Song song1 = parser.parseSong("song.txt");
        assertTrue(song.equals(song1));
    }

    @Test
    public void testParseSongs() throws IOException
    {
        parser.parseSongs("/home/pitchumani/songs/");
    }
}