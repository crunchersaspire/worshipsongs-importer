package org.worshipsongs.importer;

import java.sql.*;

/**
 * Created by Pitchu on 10/18/2015.
 */

public class AuthorDao implements IAuthorDao
{
    public int getAuthorId(Connection connection, String authorName)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM AUTHORS where display_name = '" + authorName + "';");
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        return id;
    }

    public boolean insertAuthorSongs(Connection connection, int authorId, int songId)
    {
        try {
            String query = "insert into authors_songs (author_id, song_id) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, authorId);
            preparedStatement.setInt(2, songId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e);
            return false;
        }
    }

    public int insertAuthor(Connection connection, String displayName)
    {
        try {
            String query = "insert into authors (first_name, last_name, display_name) values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "");
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, displayName);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        return getAuthorId(connection, displayName);
    }
}