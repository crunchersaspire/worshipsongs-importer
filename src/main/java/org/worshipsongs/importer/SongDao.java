package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pitchu on 10/19/2015.
 */
public class SongDao {
    public int getSongId(Connection connection, String title)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM SONGS where title = '" + title + "';" );
            if(resultSet.next()) {
                id = resultSet.getInt("id");
            }
            resultSet.close();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public boolean insertSong(Connection connection, Song song, SongBook songBook)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            String query = "insert into songs (song_book_id, title, alternate_title, lyrics, verse_order, copyright, comments, " +
                    "ccli_number, song_number, theme_name, search_title, search_lyrics, create_date, last_modified, temporary) values " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, songBook.getId());
            preparedStatement.setString(2, song.getTitle());
            preparedStatement.setString(3, song.getAlternateTitle());
            preparedStatement.setString(4, song.getXmlLyrics());
            preparedStatement.setString(5, song.getVerseOrder());
            preparedStatement.setString(6, "");
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
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}