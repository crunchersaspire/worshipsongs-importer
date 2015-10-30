package org.worshipsongs.importer;

import java.sql.Connection;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface IAuthorDao
{
    int getAuthorId(Connection connection, String authorName);
    boolean insertAuthorSongs(Connection connection, int authorId, int songId);
    int insertAuthor(Connection connection, String displayName);
}
