package org.worshipsongs.importer;

import java.sql.Connection;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ISongDao
{
    int getSongId(Connection connection, String title);
    boolean insertSong(Connection connection, Song song, int songBookId);
}
