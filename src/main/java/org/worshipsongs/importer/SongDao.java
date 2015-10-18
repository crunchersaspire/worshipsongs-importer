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
            id = resultSet.getInt("id");
            resultSet.close();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public void insertSong(Connection connection, Song song, SongBook songBook)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            Statement statement = null;

            String query = "insert into songs (song_book_id, title, alternate_title, lyrics, verse_order, copyright, comments, " +
                    "ccli_number, song_number, theme_name, search_title, search_lyrics, created_date, last_modified, temproary) values " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, songBook.getId());
            ps.setString(2, song.getTitle());
            ps.setString(3, song.getAlternateTitle());
            ps.setString(4, song.getLyrics());
            ps.setString(5, song.getVerseOrder());
            ps.setString(6, "");
            ps.setString(7, "");
            ps.setString(8, "");
            ps.setString(9, "");
            ps.setString(10, "");
            ps.setString(11, song.getSearchTitle());
            ps.setString(12, song.getSearchLyrics());
            ps.setString(13, dateFormat.format(date));
            ps.setString(14, dateFormat.format(date));
            ps.setString(15, "");
            ps.executeUpdate(query);
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
