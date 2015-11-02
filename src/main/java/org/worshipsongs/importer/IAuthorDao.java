package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface IAuthorDao
{
    Author findByDisplayName(String displayName) throws SQLException;

    void createAuthorSong(Song song) throws SQLException;

    void create(Author author) throws SQLException;
}
