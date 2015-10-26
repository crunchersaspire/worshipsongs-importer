package org.worshipsongs.importer;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
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
        int authorId;
        int topicId;
        int songBookId;
        int songId;

        connection = databaseUtils.connectDb(openLpHome);

        authorId = getAuthorId(author.getAuthor(), connection);
        if (authorId > 0) {
            author.setId(authorId);
        } else {
            author.setId(insertAuthor(connection, author.getAuthor()));
        }

        topicId = getTopicId(topic.getTopic(), connection);
        if (topicId > 0) {
            topic.setId(topicId);
        } else {
            topic.setId(insertTopic(connection, topic.getTopic()));
        }

        songBookId = getSongBookId(songBook.getSongBook(), connection);
        if (songBookId > 0) {
            songBook.setId(songBookId);
        } else {
            songBook.setId(insertSongBook(connection, songBook.getSongBook()));
        }

        if (songDao.insertSong(connection, song, songBook)) {
            songId = songDao.getSongId(connection, song.getTitle());
            if (authorDao.insertAuthorSongs(connection, author, songId)) {
                topicDao.insertTopicSongs(connection, topic, songId);
            }
        }
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
