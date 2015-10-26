package org.worshipsongs.importer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;

import static org.junit.Assert.assertEquals;

/**
 * Created by pitchumani on 10/26/15.
 */
public class SongServiceTest
{
    private SongService songService = new SongService();
    private SongParser songParser = new SongParser();
    private ClassLoader classLoader;
    private Connection connection;
    private DatabaseUtils databaseUtils = new DatabaseUtils();

    @Before
    public void setUp()
    {
        classLoader = getClass().getClassLoader();
        connection = databaseUtils.connectDb(this.getClass().getResource("/db").getPath());
    }

    @Test
    public void testGetAuthorId() throws IOException
    {
        String input = IOUtils.toString(classLoader.getResourceAsStream("song.txt"));
        assertEquals(147, songService.getAuthorId(songParser.parseAuthor(input), connection));
    }

    @Test
    public void testGetSongBookId() throws IOException
    {
        String input = IOUtils.toString(classLoader.getResourceAsStream("song.txt"));
        assertEquals(4, songService.getSongBookId(songParser.parseSongBook(input), connection));
    }

    @Test
    public void testGetTopicId() throws IOException
    {
        String input = IOUtils.toString(classLoader.getResourceAsStream("song.txt"));
        assertEquals(11, songService.getTopicId(songParser.parseTopic(input), connection));
    }
}