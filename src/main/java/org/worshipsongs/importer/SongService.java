package org.worshipsongs.importer;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Created by pitchumani on 10/26/15.
 */
public class SongService
{
    private SongDao songDao = new SongDao();
    private AuthorDao authorDao = new AuthorDao();
    private TopicDao topicDao = new TopicDao();
    private SongBookDao songBookDao = new SongBookDao();

    public void parseAndInsertSong(String songsDirectory, String dbFilePath) throws IOException
    {
        List list;
        SongParser songParser = new SongParser();
        list = songParser.readFileAndParseSong(songsDirectory);

        Song song = (Song) list.get(0);
        Topic topic = (Topic) list.get(1);
        Author author = (Author) list.get(2);
        SongBook songBook = (SongBook) list.get(3);

        insertRecords(song, topic, author, songBook, dbFilePath);
    }

    void insertRecords(Song song, Topic topic, Author author, SongBook songBook, String openLpHome)
    {
        Connection connection;
        DatabaseUtils databaseUtils = new DatabaseUtils();
        int songId;
        int authorId;
        int topicId;
        int songBookId;

        connection = databaseUtils.connectDb(openLpHome);
        authorId = getSetInsertAuthor(author, connection);
        topicId = getSetInsertTopic(topic, connection);
        songBookId = getSetInsertSongBook(songBook, connection);

        if (songDao.insertSong(connection, song, songBookId)) {
            songId = songDao.getSongId(connection, song.getTitle());
            if (authorDao.insertAuthorSongs(connection, authorId, songId)) {
                topicDao.insertTopicSongs(connection, topicId, songId);
            }
        }
    }

    int getSetInsertAuthor(Author author, Connection connection)
    {
        int authorId;
        authorId = getAuthorId(author.getAuthor(), connection);
        if (authorId > 0) {
            return authorId;
        } else {
            authorId = insertAuthor(connection, author.getAuthor());
        }
        return authorId;
    }

    int getSetInsertTopic(Topic topic, Connection connection)
    {
        int topicId;
        topicId = getTopicId(topic.getTopic(), connection);
        if (topicId > 0) {
            return topicId;
        } else {
            topicId = insertTopic(connection, topic.getTopic());
        }
        return topicId;
    }

    int getSetInsertSongBook(SongBook songBook, Connection connection)
    {
        int songBookId;
        songBookId = getSongBookId(songBook.getSongBook(), connection);
        if (songBookId > 0) {
            return songBookId;
        } else {
            songBookId = insertSongBook(connection, songBook.getSongBook());
        }
        return songBookId;
    }

    int getAuthorId(String authorName, Connection connection)
    {
        return authorDao.getAuthorId(connection, authorName);
    }

    int getSongBookId(String songBookName, Connection connection)
    {
        return songBookDao.getSongBookId(connection, songBookName);
    }

    int getTopicId(String topicName, Connection connection)
    {
        return topicDao.getTopicId(connection, topicName);
    }

    int insertAuthor(Connection connection, String author)
    {
        return authorDao.insertAuthor(connection, author);
    }

    int insertTopic(Connection connection, String topic)
    {
        return topicDao.insertTopic(connection, topic);
    }

    int insertSongBook(Connection connection, String topic)
    {
        return songBookDao.insertSongBook(connection, topic);
    }
}
