package org.worshipsongs.importer;

import java.sql.*;

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

    public SongBook findByName(String name) throws SQLException
    {
        SongBook songBook = new SongBook();
        int id = 0;
        Statement statement = null;
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SONG_BOOKS where name = '" + name + "';");
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        resultSet.close();
        statement.close();
        songBook.setId(id);
        return songBook;
    }

    public void create(SongBook songBook) throws SQLException
    {
        String query = "insert into song_books (name, publisher) values (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, songBook.getSongBook());
        preparedStatement.setString(2, "");
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
