package org.worshipsongs.importer;

import java.io.IOException;

/**
 * Created by pitchumani on 10/12/15.
 */
public class ParseSong
{
    public static void main(String args[]) throws IOException
    {
        SongParser parser = new SongParser();
        if(args.length > 0) {
            parser.parseSongs(args[0] + "/");
        }
        else {
            System.out.println("Songs directory argument should not be empty");
        }
    }
}
