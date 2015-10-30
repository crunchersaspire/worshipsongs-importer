package org.worshipsongs.importer;

import java.sql.Connection;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface IAuthorDao
{
    Author getAuthor(Author author);

    boolean insertAuthorSongs(Song song);

    Author insertAuthor(Author author);
}
