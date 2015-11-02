package org.worshipsongs.importer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ISongParser
{
    Song parseSong(File file) throws IOException, SQLException;
}
