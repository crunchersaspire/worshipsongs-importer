package org.worshipsongs.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pitchumani on 10/12/15.
 */
public class Main
{
    public static void main(String args[]) throws IOException
    {
        SongService songService = new SongService();
        if (args.length < 2) {
            System.out.println("Below inputs are mandatory.");
            System.out.println("1.Songs directory path");
            System.out.println("2.Database file path.");
            System.exit(0);
        }

        if (new File(args[0]).exists()) {
            if (new File(args[1]).exists()) {
                if (new File(args[0]).listFiles().length > 0) {
                    songService.parseAndInsertSongs(args[0] + "/", args[1]);
                } else {
                    System.out.println("Have no files in songs directory.");
                }
            } else {
                System.out.println(args[1] + " does not exist.");
            }
        } else {
            System.out.println(args[0] + " does not exist.");
        }
    }
}