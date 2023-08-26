package com.dal.filedbms;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A  class for executing Delete query for deleting rows
 */

public class DeleteQueryExecuter {

    QueryManager q = new QueryManager();
    /**
     * deletes row when user enter delete query.
     *
     * @param queryEntered query to be executed.
     * @param activeUser current logged in user.
     * @throws IOException If there was an error writing to the file.
     */
    public void executeDeleteQuery(String queryEntered, User activeUser){

        if (queryEntered.contains(";")) {
            queryEntered = queryEntered.replace(";","");
        }
        queryEntered = queryEntered.toLowerCase();
        String tableName;
        boolean tableExist = false;

        List<String> tempArray = new ArrayList<>();
        String whereCondition = "";
        String columnToCompare = "";
        String operator = "";
        int indexOfColumnToCompare = 0;
        String valueStringToCompare = "";

        tableName = queryEntered.trim().substring(queryEntered.toLowerCase().indexOf("from") + 4,queryEntered.toLowerCase().indexOf("where")).trim();
        whereCondition=queryEntered.substring(queryEntered.toLowerCase().indexOf("where") + 5);

        System.out.println("Table name : " + tableName);

        System.out.println("Where condition :" + whereCondition);

        String tableNameToSearch = activeUser.getEmail()+"-"+tableName+".jpd";
        String tableValueFileName = activeUser.getEmail()+ "-" + tableName +"-content"+ ".jpd";
        //String newTableValueFile  = activeUser.getEmail()+ "-" + tableName +"-content-new"+ ".jpd";

        File tableToSearch = new File(tableNameToSearch);

        File valueTableToSearch = new File(tableValueFileName);

        if(tableToSearch.exists() && !tableToSearch.isDirectory()) {
            tableExist = true;
            System.out.println("Table found!");

            LogManager.log("Table found :"+tableName,activeUser.getEmail());


            String[] whereConditionSplit = {};
            String[] operatorTypes = {"=", "!=", "<", ">", ">=", "<=", "between", "in", "like"};

            for (String s : operatorTypes) {
                if (whereCondition.contains(s)) {
                    whereConditionSplit = whereCondition.trim().split(s);
                    operator = s;
                    columnToCompare = whereConditionSplit[0].trim();
                    valueStringToCompare = whereConditionSplit[1].trim();

                }
            }
            System.out.println("operator matched: " + operator);
            System.out.println("value to compare: " + valueStringToCompare);
            System.out.println("column to compare: " + columnToCompare);

            try (BufferedReader br2 = new BufferedReader(new FileReader(tableToSearch))) {
                String[] columnList = br2.readLine().trim().split("#");
                indexOfColumnToCompare = Arrays.binarySearch(columnList, columnToCompare);
                System.out.println("index to compare: "+indexOfColumnToCompare);

            } catch (Exception e) {
                System.out.println("An error occurred while reading the file: " + e.getMessage());
                q.handleQuery(activeUser);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(tableValueFileName))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] rowParts = line.split("\\s*#\\s*");

                    if (!conditionCaseCheck(operator, indexOfColumnToCompare, valueStringToCompare, rowParts)) {
                        System.out.println("Row: " + Arrays.asList(rowParts));
                        tempArray.add(line);
                    }

                }

                LogManager.log("Query Executed Successfully",activeUser.getEmail());

            } catch (Exception e) {
                System.out.println("An error occurred while reading the file: " + e.getMessage());
                q.handleQuery(activeUser);
            }

            try  {
                FileWriter fw = new FileWriter(tableValueFileName,false);
                fw.write("");
            } catch (Exception e) {
                e.printStackTrace();
                q.handleQuery(activeUser);
            }

            try (FileWriter fw = new FileWriter(tableValueFileName)) {
                for (String line : tempArray) {
                    fw.write(line + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                q.handleQuery(activeUser);
            }


        }



    }

    /**
     * checks if where condition is true for that particular row.
     *
     * @param operator used in the where condition
     * @param indexOfColumnToCompare in where clause
     * @param valueStringToCompare in where clause
     * @param rowParts array of values in that row
     * @return boolean if row passes the where condition
     */

    public boolean conditionCaseCheck(String operator, int indexOfColumnToCompare, String valueStringToCompare, String[] rowParts) {

        boolean conditionSatisfied = false;
        //System.out.println(operator +":"+ indexOfColumnToCompare +":"+ valueStringToCompare);
        switch (operator) {

            case "=":
                conditionSatisfied = rowParts[indexOfColumnToCompare].equalsIgnoreCase(valueStringToCompare) ? true : false;
                //System.out.println("********** "+conditionSatisfied);
                break;

            case "!=":
                conditionSatisfied = rowParts[indexOfColumnToCompare].equalsIgnoreCase(valueStringToCompare) ? false : true;
                break;

            case "<":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare])<Integer.parseInt(valueStringToCompare) ? true : false;
                break;

            case ">":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare])>Integer.parseInt(valueStringToCompare) ? true : false;
                break;

            case ">=":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare])>=Integer.parseInt(valueStringToCompare) ? true : false;
                break;

            case "<=":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare])<=Integer.parseInt(valueStringToCompare) ? true : false;
                break;
            default:
                System.out.println("unsupported operator:"+operator);
        }
        return conditionSatisfied;
    }
    /** references :
        https://www.w3schools.com/sql/sql_join.asp
        https://www.w3schools.com/java/java_files.asp
        https://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html

    */
}
