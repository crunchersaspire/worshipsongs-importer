package org.worshipsongs.importer;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pitchu on 10/19/2015.
 */
public class SongDao implements ISongDao
{
    private Connection connection;
    private IAuthorDao authorDao;
    private ITopicDao topicDao;
    private ISongBookDao songBookDao;

    public SongDao(Connection connection)
    {
        this.connection = connection;
        authorDao = new AuthorDao(connection);
        topicDao = new TopicDao(connection);
        songBookDao = new SongBookDao(connection);
    }

    public Song findByTitle(Song title) throws SQLException
    {
        int id = 0;
        Statement statement = null;
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SONGS where title = '" + title.getTitle() + "';");
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        resultSet.close();
        statement.close();
        title.setId(id);
        return title;
    }

    public void insertSong(Song song) throws SQLException
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String query = "insert into songs (song_book_id, title, alternate_title, lyrics, verse_order, copyright, comments, " +
                "ccli_number, song_number, theme_name, search_title, search_lyrics, create_date, last_modified, temporary) values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, song.getSongBook().getId());
        preparedStatement.setString(2, song.getTitle());
        preparedStatement.setString(3, song.getAlternateTitle());
        preparedStatement.setString(4, song.getXmlLyrics());
        preparedStatement.setString(5, song.getVerseOrder());
        preparedStatement.setString(6, "");
        //preparedStatement.setString(7, song.getComment());
        preparedStatement.setString(7, "");
        preparedStatement.setString(8, "");
        preparedStatement.setString(9, "");
        preparedStatement.setString(10, "");
        preparedStatement.setString(11, song.getSearchTitle());
        preparedStatement.setString(12, song.getSearchLyrics());
        preparedStatement.setString(13, dateFormat.format(date));
        preparedStatement.setString(14, dateFormat.format(date));
        preparedStatement.setInt(15, 0);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    void create(Song song) throws SQLException
    {
        Author author = new Author();
        Topic topic = new Topic();

        song = findByTitle(song);
        if (author.getId() != 0) {
            authorDao.createAuthorSong(song);
        }
        if (topic.getId() != 0) {
            topicDao.createTopicSongs(song);
        }
    }
}