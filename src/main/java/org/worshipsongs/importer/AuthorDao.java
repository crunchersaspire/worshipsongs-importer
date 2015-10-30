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

    public Author getAuthor(Author author)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM AUTHORS where display_name = '" + author.getAuthor() + "';");
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        author.setId(id);
        return author;
    }

    public boolean insertAuthorSongs(Song song)
    {
        try {
            String query = "insert into authors_songs (author_id, song_id) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, song.getAuthor().getId());
            preparedStatement.setInt(2, song.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e);
            return false;
        }
    }

    public Author insertAuthor(Author author)
    {
        try {
            String query = "insert into authors (first_name, last_name, display_name) values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "");
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, author.getAuthor());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        return getAuthor(author);
    }
}