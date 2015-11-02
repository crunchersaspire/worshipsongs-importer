package org.worshipsongs.importer;

import java.sql.*;

/**
 * Created by Pitchu on 10/18/2015.
 */

public class AuthorDao implements IAuthorDao
{
    private Connection connection;
    public AuthorDao(Connection connection)
    {
        this.connection = connection;
    }

    public Author findByDisplayName(String displayName) throws SQLException
    {
        Author author = new Author();
        int id = 0;
        Statement statement = null;
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM AUTHORS where display_name = '" + displayName + "';");
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        resultSet.close();
        statement.close();
        author.setId(id);
        return author;
    }

    public void createAuthorSong(Song song) throws SQLException
    {
        String query = "insert into authors_songs (author_id, song_id) values (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, song.getAuthor().getId());
        preparedStatement.setInt(2, song.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void create(Author author) throws SQLException
    {
        String query = "insert into authors (first_name, last_name, display_name) values (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, "");
        preparedStatement.setString(2, "");
        preparedStatement.setString(3, author.getAuthor());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}