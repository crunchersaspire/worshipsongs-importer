package org.worshipsongs.importer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by pitchumani on 10/9/15.
 */
public class SongTest
{
    private SongParser parser = new SongParser();
    private String lyrics = "[V1]\n" +
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

    private String searchLyrics = "lord i lift your name on high lord i love to sing your praises im so glad youre in my life im so glad you came to save us you came from heaven to earth to show the way from the earth to the cross my debts to pay from the cross to the grave from the grave to the sky lord i lift your name on high";

    private String xmlLyrics = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><song version=\"1.0\"><lyrics><verse label=\"1\" type=\"v\"><![CDATA[Lord I lift Your name on high\n" +
            "Lord I love to sing Your praises]]></verse><verse label=\"1\" type=\"o\"><![CDATA[I’m so glad You're in my life\n" +
            "I’m so glad You came to save us]]></verse><verse label=\"1\" type=\"c\"><![CDATA[You came from heaven to earth\n" +
            "To show the way]]></verse><verse label=\"2\" type=\"o\"><![CDATA[From the earth to the cross,\n" +
            "My debts to pay]]></verse><verse label=\"3\" type=\"o\"><![CDATA[From the cross to the grave,\n" +
            "From the grave to the sky\n" +
            "Lord I lift Your name on high]]></verse></lyrics></song>";
    Song song = new Song();
    Song song1 = new Song();
    private ClassLoader classLoader;

    @Before
    public void setUp() throws IOException
    {
        classLoader = getClass().getClassLoader();
        song.setTitle("Lord I lift Your Name");
        song.setAlternateTitle("Lord I lift Your Name");
        song.setVerseOrder("V1 O1 C1 O2 O3");
        song.setXmlLyrics(xmlLyrics);
        song.setSearchTitle((song.getTitle() + "@" + song.getAlternateTitle()).toLowerCase());
        song.setSearchLyrics(searchLyrics);
        String input = IOUtils.toString(classLoader.getResourceAsStream("song.txt"));
        List list = parser.parseSong(input);
        song1 = (Song) list.get(0);
    }

    @Test
    public void testEquals() throws IOException
    {
        assertTrue(song.equals(song1));
    }

    @Test
    public void testNotEquals() throws IOException
    {
        song.setTitle("Bar");
        assertFalse(song.equals(song1));
    }

    @Test
    public void testToString()
    {
        assertEquals("Title --> Lord I lift Your Name", song.toString());
    }
}
