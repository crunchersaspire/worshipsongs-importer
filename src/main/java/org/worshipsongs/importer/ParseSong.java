package org.worshipsongs.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pitchumani on 10/12/15.
 */
public class ParseSong
{
    public static void main(String args[]) throws IOException
    {
        List title = new ArrayList();
        SongParser parser = new SongParser();
        if(args.length > 0) {
            title = parser.parseSongs(args[0] + "/");
        }
        else {
            System.out.println("Songs directory argument should not be empty");
        }
        for (int i = 0; i < title.size(); i++)
            System.out.println(title.get(i));
    }
}