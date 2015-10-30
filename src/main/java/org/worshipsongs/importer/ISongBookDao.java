package org.worshipsongs.importer;

import java.sql.Connection;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ISongBookDao
{
    SongBook getSongBook(SongBook songBook);

    SongBook insertSongBook(SongBook songBook);
}
