package com.dal.filedbms;

import java.io.*;
import java.util.Date;


/**
 * A  class for recording all the logs
 */

public class LogManager {

    private static final String LOG_FILE = "logs.txt";

    /**
     * creates logs in logs file
     *
     * @param newLog message to write as log.
     * @param userEmail current logged in user eamil.
     * @throws IOException If there was an error writing to the file.
     */

    public static void log(String newLog, String userEmail) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
            writer.newLine();
            writer.write(">" + userEmail + ": " + new Date() + ": " + newLog);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }

    }

}
