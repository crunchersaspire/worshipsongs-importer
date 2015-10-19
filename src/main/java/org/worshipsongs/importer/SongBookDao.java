package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Pitchu on 10/18/2015.
 */
public class SongBookDao {
    public int getSongBookId(Connection connection, String songBook)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM SONG_BOOKS where name = '" + songBook + "';" );
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
