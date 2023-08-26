package com.dal.filedbms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A  class for executing Insert query for inserting data to existing table
 */

public class InsertQueryExecuter {

    QueryManager q = new QueryManager();
    /**
     * inserts new rows in existing table when user enter insert query.
     *
     * @param queryEntered query to be executed.
     * @param activeUser current logged in user.
     * @throws IOException If there was an error writing to the file.
     */

    public boolean executeInsertQuery(String queryEntered,  User activeUser){


        String tableName;
        boolean tableExist = false;
        List<String> rowItems = new ArrayList<>();

        String[] querySplit = queryEntered.trim().split("\\(", 2);
        String[] queryPart1 = querySplit[0].trim().split(" ");
        String[] queryPart2 = querySplit[1].trim().split(",");

        tableName=queryPart1[2];
        System.out.println("Table name : " + tableName);

        String tableNameToSearch = activeUser.getEmail()+"-"+tableName+".jpd";


        File tableToSearch = new File(tableNameToSearch);

        if(tableToSearch.exists() && !tableToSearch.isDirectory()){

            tableExist=true;

            queryPart2[queryPart2.length-1] = queryPart2[queryPart2.length-1].substring(0,(queryPart2[queryPart2.length-1].length())-2);

            for (int i = 0; i < queryPart2.length; i++) {
                queryPart2[i] = queryPart2[i].replaceAll("'", "");
                System.out.print(queryPart2[i] +", ");

            }

            String fileName = activeUser.getEmail()+ "-" + tableName +"-content"+ ".jpd";

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));

                String line1 = "";
                for (int i = 0; i < queryPart2.length; i++) {
                    line1=line1+queryPart2[i].toString()+"#";
                }
                LogManager.log("Executing the Query!!",activeUser.getEmail());

                //System.out.println(line1);
                writer.write(line1);
                writer.newLine();

                LogManager.log("Values Inserted in the table :"+tableName+" by user: "+activeUser.getEmail(),activeUser.getEmail());
                writer.close();
            } catch (Exception e) {
                System.out.println("Error Writing File:"+e);
                q.handleQuery(activeUser);
            }

        }else {
            System.out.println("Table  Does not exist!");
            q.handleQuery(activeUser);

        }
        return tableExist;
    }
    /** references :
     https://www.w3schools.com/sql/sql_join.asp
     https://www.w3schools.com/java/java_files.asp
     https://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html

     */

}
