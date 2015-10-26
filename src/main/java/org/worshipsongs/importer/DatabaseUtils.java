package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by pitchumani on 10/26/15.
 */
public class DatabaseUtils
{
    public Connection connectDb(String openlp_home)
    {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + openlp_home + "/songs.sqlite");
        } catch ( Exception e ) {
            System.out.println(e);
        }
        return  connection;
    }
}
