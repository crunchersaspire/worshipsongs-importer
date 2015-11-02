package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ISongBookDao
{
    SongBook findByName(String name) throws SQLException;

    void create(SongBook songBook) throws SQLException;
}
