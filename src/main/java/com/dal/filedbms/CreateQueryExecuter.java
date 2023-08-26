package com.dal.filedbms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A  class for executing Create table query
 */
public class CreateQueryExecuter {

    QueryManager qm = new QueryManager();

    /**
     * creates new tables when user enter create table query.
     *
     * @param queryEntered query to be executed.
     * @param activeUser current logged in user.
     * @throws IOException If there was an error writing to the file.
     */
    public void executeCreateTableQuery(String queryEntered, User activeUser){


        File file;

        String tableName;
        List<String> columnName = new ArrayList<>();
        List<String> columnType = new ArrayList<>();

        String[] querySplit = queryEntered.trim().split("\\(", 2);
        String[] queryPart1 = querySplit[0].trim().split(" ");
        String[] queryPart2 = querySplit[1].trim().split(",");

        tableName = queryPart1[2];
        System.out.println("Table name : " + tableName);

        for (int i = 0; i < queryPart2.length; i++) {
            String[] columnSplit = queryPart2[i].trim().split(" ");
            String[] checkSymbolArray = {",", ";", ")", "("};
            if (!Arrays.asList(checkSymbolArray).contains(columnSplit[0])) {
                columnName.add(columnSplit[0].trim());
                columnSplit[1] = columnSplit[1].replace(");", "");
                columnType.add(columnSplit[1].trim());
                System.out.println(columnSplit[0] + ":" + columnSplit[1]);
            }
        }
        String fileName = activeUser.getEmail()+ "-" + tableName + ".jpd";
        file = new File(fileName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));

            String line1 = "";
            for (int i = 0; i < columnName.size(); i++) {
                line1=line1+columnName.get(i).toString()+"#";
            }
            LogManager.log("Executing the Query!!",activeUser.getEmail());


            System.out.println(line1);
            writer.write(line1);
            writer.newLine();


            String line2 = "";
            for (int i = 0; i < columnType.size(); i++) {
                line2=line2+columnType.get(i).toString()+"#";
            }
            System.out.println(line2);
            writer.write(line2);
            LogManager.log("Table Created with name:"+activeUser.getEmail()+ "-" + tableName + ".jpd",activeUser.getEmail());
            writer.close();
        } catch (IOException e) {
            System.out.println("Error Writing File:"+e);
            qm.handleQuery(activeUser);
        }


    }


}
