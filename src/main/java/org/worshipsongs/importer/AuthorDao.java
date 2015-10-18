package org.worshipsongs.importer;

import java.sql.*;

/**
 * Created by Pitchu on 10/18/2015.
 */

public class AuthorDao {
    public String getEnvironmentVariable(String variableName)
    {
        return System.getProperty(variableName);
    }

    public Connection connectDb(String openlp_home)
    {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            // C:\Users\Pitchu\AppData\Roaming\openlp\data\songs
            connection = DriverManager.getConnection("jdbc:sqlite:" + openlp_home + "/songs.sqlite");
        } catch ( Exception e ) {
            System.out.println(e);
        }
        return  connection;
    }

    public int getAuthorId(Connection connection, String authorName)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM AUTHORS where display_name = '" + authorName + "';" );
            id = resultSet.getInt("id");
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public void insertAuthor(Connection connection, Author author, int songId)
    {
        try {
            String query = "insert into authors_songs (author_id, song_id) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, author.getId());
            ps.setInt(2, songId);
            ps.executeUpdate();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}