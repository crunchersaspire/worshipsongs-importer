package org.worshipsongs.importer;

/**
 * Created by Pitchu on 10/18/2015.
 */

public class AuthorDao {
    public String checkEnvironmentVariable(String variableName)
    {
        return System.getProperty(variableName);
    }

    public static void main(String args[])
    {
        AuthorDao authorDao = new AuthorDao();
        String environmentVariable = authorDao.checkEnvironmentVariable("OPENLP_HOME");
        if(environmentVariable.isEmpty())
            System.out.println("OPENLP_HOME environment variable should be available.");
        else
            System.out.println("OPENLP_HOME = "+environmentVariable);
    }
}