package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}