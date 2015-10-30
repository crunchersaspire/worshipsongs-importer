package org.worshipsongs.importer;

import java.sql.Connection;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ISongBookDao
{
    int getSongBookId(Connection connection, String songBook);
    int insertSongBook(Connection connection, String songBook);
}
