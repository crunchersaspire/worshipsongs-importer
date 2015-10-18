package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.DriverManager;

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
}