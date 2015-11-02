package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ISongDao
{
    Song findByTitle(Song title) throws SQLException;

    void insertSong(Song song) throws SQLException;
}
