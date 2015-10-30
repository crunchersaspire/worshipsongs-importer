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
    public void testFindOrCreateAuthor() throws IOException
    {
        Author author = new Author();
        author.setAuthor("Author Unknown");
        assertEquals(1, songParser.findOrCreateAuthor(author));
    }

    @Test
    public void testFindOrCreateSongBook() throws IOException
    {
        SongBook songBook = new SongBook();
        songBook.setSongBook("Foo");
        assertEquals(1, songParser.findOrCreateSongBook(songBook));
    }

    @Test
    public void testFindOrCreateTopic() throws IOException
    {
        Topic topic = new Topic();
        topic.setTopic("Foo");
        assertEquals(1, songParser.findOrCreateTopic(topic));
    }
}