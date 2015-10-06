package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class SongParserTest
{
    private SongParser parser = new SongParser();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    String lyrics = "And were this world all devils o’er,\n" +
            "And watching to devour us,\n" +
            "We lay it not to heart so sore;\n" +
            "Not they can overpower us.\n" +
            "And let the prince of ill\n" +
            "Look grim as e’er he will,\n" +
            "He harms us not a whit;\n" +
            "For why? his doom is writ;\n" +
            "A word shall quickly slay him.\n" +
            "\n" +
            "With force of arms we nothing can,\n" +
            "Full soon were we down-ridden;\n" +
            "But for us fights the proper Man,\n" +
            "Whom God Himself hath bidden.\n" +
            "Ask ye: Who is this same?\n" +
            "Christ Jesus is His name,\n" +
            "The Lord Sabaoth’s Son;\n" +
            "He, and no other one,\n" +
            "Shall conquer in the battle.\n" +
            "\n" +
            "And were this world all devils o’er,\n" +
            "And watching to devour us,\n" +
            "We lay it not to heart so sore;\n" +
            "Not they can overpower us.\n" +
            "And let the prince of ill\n" +
            "Look grim as e’er he will,\n" +
            "He harms us not a whit;\n" +
            "For why? his doom is writ;\n" +
            "A word shall quickly slay him.\n" +
            "\n" +
            "God’s word, for all their craft and force,\n" +
            "One moment will not linger,\n" +
            "But, spite of hell, shall have its course;\n" +
            "’Tis written by His finger.\n" +
            "And though they take our life,\n" +
            "Goods, honour, children, wife,\n" +
            "Yet is their profit small:\n" +
            "These things shall vanish all;\n" +
            "The city of God remaineth.";
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
    public void testParseSearchLyrics()
    {
        assertEquals("", parser.parseSearchLyrics(""));
        assertEquals("Foo bar", parser.parseSearchLyrics("searchLyrics=Foo bar"));
        assertEquals("", parser.parseSearchLyrics("searchLyrics:Foo bar"));
        assertEquals("Foo bar", parser.parseSearchLyrics("barbarbar\n" +
                "searchLyrics=Foo bar\n" +
                "foofoo"));
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
        assertEquals("", parser.parseLyrics(""));
    }
}