package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Pitchu on 10/18/2015.
 */
public class SongBookDao implements ISongBookDao
{
    private Connection connection;
    public SongBookDao(Connection connection)
    {
        this.connection = connection;
    }

    public SongBook getSongBook(SongBook songBook)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM SONG_BOOKS where name = '" + songBook.getSongBook() + "';");
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        songBook.setId(id);
        return songBook;
    }

    public SongBook insertSongBook(SongBook songBook)
    {
        try {
            String query = "insert into song_books (name, publisher) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, songBook.getSongBook());
            preparedStatement.setString(2, "");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        return getSongBook(songBook);
    }
}
