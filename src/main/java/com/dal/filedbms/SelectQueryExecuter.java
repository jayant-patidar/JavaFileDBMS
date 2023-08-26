package com.dal.filedbms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A  class for executing Select query
 */

public class SelectQueryExecuter {

    QueryManager q = new QueryManager();
    /**
     * prints the asked data by executing select query and handles the conditions
     *
     * @param queryEntered query to be executed.
     * @param activeUser current logged in user.
     * @throws IOException If there was an error writing to the file.
     */

    public void executeSelectQuery(String queryEntered, User activeUser) {

        if (queryEntered.contains(";")) {
            queryEntered = queryEntered.substring(0, queryEntered.length() - 1);
        }

        boolean tableExist = false;

        queryEntered = queryEntered.toLowerCase();
        String[] querySplit = queryEntered.split("\\s+");
        String tableName = "";
        String[] columnsToPrint = {};
        List<Integer> indexOfColumnsToPrint = new ArrayList<>();
        boolean selectAll = false;
        String whereCondition1 = "";
        String whereCondition2 = "";

        String operator1 = "";
        String columnToCompare1 = "";
        int indexOfColumnToCompare1 = 0;
        String valueStringToCompare1 = "";

        String operator2 = "";
        String columnToCompare2 = "";
        int indexOfColumnToCompare2 = 0;
        String valueStringToCompare2 = "";

        boolean whereFound = false;

        //for loop to parse and extract all data from query for every case and store in variables;
        for (int i = 0; i < querySplit.length; i++) {
            if (querySplit[i].equalsIgnoreCase("FROM")) {
                tableName = querySplit[i + 1].trim();
            }
            else if (querySplit[i].equalsIgnoreCase("SELECT")) {
                if (queryEntered.contains("*")) {
                    selectAll = true;

                }
                else {
                    columnsToPrint = queryEntered.substring(queryEntered.indexOf("select") + 6, queryEntered.indexOf("from")).split(",");
                    for (int j = 0; j < columnsToPrint.length; j++) {
                        columnsToPrint[j] = columnsToPrint[j].trim();
                    }
                    //System.out.println("Columns to print"+Arrays.asList(columnsToPrint));
                }
            }
            else if (querySplit[i].equalsIgnoreCase("WHERE")) {
                String whereQuery = queryEntered.substring(queryEntered.toLowerCase().indexOf("where") + 5);
                //System.out.println("Where Query : "+whereQuery);
                whereFound = true;
                if (whereQuery.toLowerCase().contains("and")) {

                    whereCondition1 = whereQuery.trim().substring(0, whereQuery.indexOf("and") - 1).trim();
                    //System.out.println("Where condition1 : " + whereCondition1);

                    whereCondition2 = whereQuery.trim().substring(whereQuery.indexOf("and") + 3);
                    //System.out.println("Where condition2 : " + whereCondition2);

                    //System.out.println(Arrays.asList(columnsToPrint));

                    String[] whereCondition1Split = {};
                    String[] whereCondition2Split = {};
                    String[] operatorTypes = {"=", "!=", "<", ">", ">=", "<=", "between", "in", "like"};

                    for (String s : operatorTypes) {
                        if (whereCondition1.contains(s)) {
                            whereCondition1Split = whereCondition1.trim().split(s);
                            operator1 = s;
                            columnToCompare1 = whereCondition1Split[0].trim();
                            valueStringToCompare1 = whereCondition1Split[1].trim();
                            //System.out.println("operator1 matched: " +operator1);

                        }
                        if (whereCondition2.contains(s)) {
                            whereCondition2Split = whereCondition2.trim().split(s);
                            operator2 = s;
                            columnToCompare2 = whereCondition2Split[0].trim();
                            valueStringToCompare2 = whereCondition2Split[1].trim();
                            //System.out.println("operator2 matched: " +operator2);

                        }
                    }
                }
                else {

                    whereCondition1 = whereQuery.trim();
                    //System.out.println("Only one where");

                    String[] whereCondition1Split = {};
                    String[] operatorTypes = {"=", "!=", "<", ">", ">=", "<=", "between", "in", "like"};

                    for (String s : operatorTypes) {
                        if (whereCondition1.contains(s)) {
                            whereCondition1Split = whereCondition1.split(s);
                            operator1 = s;
                            columnToCompare1 = whereCondition1Split[0];
                            valueStringToCompare1 = whereCondition1Split[1];
                        }
                    }
                }
            }
        }

        //finding the table
        System.out.println("Table name: " + tableName);

        String tableFileName = activeUser.getEmail() + "-" + tableName + ".jpd";
        String tableValueFileName = activeUser.getEmail() + "-" + tableName + "-content" + ".jpd";

        File tableToSearch = new File(tableFileName);

        //Executing Select query for different cases
        if (tableToSearch.exists() && !tableToSearch.isDirectory()) {
            tableExist = true;
            System.out.println("Found table!");
            LogManager.log(tableName+" Table found !",activeUser.getEmail());

            // * & NO where  -- DONE
            if (selectAll && !queryEntered.contains("where")) {
                try (BufferedReader br1 = new BufferedReader(new FileReader(tableFileName))) {

                    columnsToPrint = br1.readLine().trim().split("#");
                    System.out.println("----------Result---------");
                    System.out.println(Arrays.asList("Columns :"+ columnsToPrint));

                } catch (IOException e) {
                    System.out.println("An error occurred while reading the file: " + e.getMessage());
                    q.handleQuery(activeUser);
                }
                try (BufferedReader br2 = new BufferedReader(new FileReader(tableValueFileName))) {

                    String line;
                    while ((line = br2.readLine()) != null) {
                        String[] row = line.trim().split("#");
                        System.out.println("Row:" + Arrays.toString(row));
                    }
                    LogManager.log("Query Executed Successfully!",activeUser.getEmail());
                } catch (Exception e) {
                    System.out.println("An error occurred while reading the file: " + e.getMessage());
                    q.handleQuery(activeUser);
                }
            }

            // * with where -- DONE
            else if (selectAll && queryEntered.contains("where")) {
                try (BufferedReader br1 = new BufferedReader(new FileReader(tableFileName))) {

                    columnsToPrint = br1.readLine().trim().split("#");
                    //System.out.println("----------Result---------");
                    //System.out.println("Columns:" + Arrays.asList(columnsToPrint));

                } catch (IOException e) {
                    System.out.println("An error occurred while reading the file: " + e.getMessage());
                    q.handleQuery(activeUser);
                }
                try (BufferedReader br2 = new BufferedReader(new FileReader(tableFileName))) {
                    String[] columnList = br2.readLine().trim().split("#");
                    indexOfColumnToCompare1 = Arrays.binarySearch(columnList, columnToCompare1);
                    //System.out.println("Columns to compare 1: "+columnToCompare1);
                    //System.out.println("index to compare 1: "+indexOfColumnToCompare1);
                    indexOfColumnToCompare2 = Arrays.binarySearch(columnList, columnToCompare2);
                    //System.out.println("Columns to compare 2: "+columnToCompare2);
                    //System.out.println("index to compare 1: "+indexOfColumnToCompare2);

                } catch (IOException e) {
                    System.out.println("An error occurred while reading the file: " + e.getMessage());
                    q.handleQuery(activeUser);
                }
                if (queryEntered.contains("and")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(tableValueFileName))) {
                        String line;
                        System.out.println("----------Result---------");
                        System.out.println("Columns: " + Arrays.asList(columnsToPrint));
                        while ((line = reader.readLine()) != null) {
                            String[] rowParts = line.split("\\s*#\\s*");
                            if (condition1CaseCheck(operator1, indexOfColumnToCompare1, valueStringToCompare1, rowParts)) {
                                if (condition2CaseCheck(operator2, indexOfColumnToCompare2, valueStringToCompare2, rowParts)) {
                                    //List<String> resultArray = new ArrayList<>();
                                    System.out.println("Row: " + Arrays.asList(rowParts));
                                }
                            }
                        }
                        LogManager.log("Query Executed Successfully",activeUser.getEmail());
                    } catch (IOException e) {
                        System.out.println("An error occurred while reading the file: " + e.getMessage());
                        q.handleQuery(activeUser);
                    }
                }
                else {
                    try (BufferedReader reader = new BufferedReader(new FileReader(tableValueFileName))) {
                        String line;
                        System.out.println("----------Result---------");
                        System.out.println("Columns: " + Arrays.asList(columnsToPrint));
                        while ((line = reader.readLine()) != null) {
                            String[] rowParts = line.split("\\s*#\\s*");
                            if (condition1CaseCheck(operator1, indexOfColumnToCompare1, valueStringToCompare1, rowParts)) {
                                //List<String> resultArray = new ArrayList<>();
                                System.out.println("Row: " + Arrays.asList(rowParts));
                            }
                        }
                        LogManager.log("Query Executed Successfully",activeUser.getEmail());
                    } catch (IOException e) {
                        System.out.println("An error occurred while reading the file: " + e.getMessage());
                        q.handleQuery(activeUser);
                    }
                }
            }
            //no * & NO where ---DONE
            else if (!selectAll && !queryEntered.contains("where")) {
                for (String c : columnsToPrint) {
                    c = c.trim();
                    try (BufferedReader br2 = new BufferedReader(new FileReader(tableFileName))) {

                        String[] columnList = br2.readLine().trim().split("#");
                        for (int i = 0; i < columnList.length; i++) {
                            if (c.equalsIgnoreCase(columnList[i])) {
                                indexOfColumnsToPrint.add(i);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred while reading the file: " + e.getMessage());
                        q.handleQuery(activeUser);
                    }
                }
                System.out.print("Columns : ");
                System.out.println(Arrays.asList(columnsToPrint));
                try (BufferedReader reader = new BufferedReader(new FileReader(tableValueFileName))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        List<String> resultArray = new ArrayList<>();
                        String[] rowParts = line.split("#");
                        for (int i = 0; i < indexOfColumnsToPrint.size(); i++) {
                            resultArray.add(rowParts[indexOfColumnsToPrint.get(i)]);
                        }
                        System.out.println("Row: " + resultArray);
                    }
                    LogManager.log("Query Executes Successfully!",activeUser.getEmail());
                } catch (IOException e) {
                    System.out.println("An error occurred while reading the file: " + e.getMessage());
                    q.handleQuery(activeUser);
                }
            }

            //no * with where --- DONE
            else if (!selectAll && queryEntered.contains("where")) {

                try (BufferedReader br2 = new BufferedReader(new FileReader(tableFileName))) {

                    String[] columnList = br2.readLine().trim().split("#");

                    indexOfColumnToCompare1 = Arrays.binarySearch(columnList, columnToCompare1);
                    //System.out.println("Columns to compare 1: "+columnToCompare1);
                    //System.out.println("index to compare 1: "+indexOfColumnToCompare1);
                    indexOfColumnToCompare2 = Arrays.binarySearch(columnList, columnToCompare2);
                    //System.out.println("Columns to compare 2: "+columnToCompare2);
                    //System.out.println("index to compare 2: "+indexOfColumnToCompare2);
                    for (String c : columnsToPrint) {
                        c = c.trim();

                        for (int i = 0; i < columnList.length; i++) {
                            if (c.equalsIgnoreCase(columnList[i])) {
                                indexOfColumnsToPrint.add(i);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while reading the file: " + e.getMessage());
                    q.handleQuery(activeUser);
                }
                if (queryEntered.contains("and")) {

                    try (BufferedReader reader = new BufferedReader(new FileReader(tableValueFileName))) {
                        String line;
                        //System.out.println("Index of Columns to print: " + Arrays.asList(indexOfColumnsToPrint));
                        System.out.println("----------Result---------");
                        System.out.println("Columns: " + Arrays.asList(columnsToPrint));
                        while ((line = reader.readLine()) != null) {

                            String[] rowParts = line.split("\\s*#\\s*");
                            /*
                            System.out.println("operator1: "+operator1);
                            System.out.println("operator2: "+operator2);
                            System.out.println("index to compare 1: "+indexOfColumnToCompare1);
                            System.out.println("index to compare 1: "+indexOfColumnToCompare2);
                            System.out.println("value to compare 1: "+valueStringToCompare1);
                            System.out.println("value to compare 2: "+valueStringToCompare2);
                            */
                            if (condition1CaseCheck(operator1, indexOfColumnToCompare1, valueStringToCompare1, rowParts)) {

                                if (condition2CaseCheck(operator2, indexOfColumnToCompare2, valueStringToCompare2, rowParts)) {
                                    List<String> resultArray = new ArrayList<>();

                                    for (int i = 0; i < indexOfColumnsToPrint.size(); i++) {
                                        resultArray.add(rowParts[indexOfColumnsToPrint.get(i)]);
                                    }
                                    System.out.println("Row: " + resultArray);
                                }
                            }
                        }
                        LogManager.log("Query Executed Successfully",activeUser.getEmail());

                    } catch (IOException e) {
                        System.out.println("An error occurred while reading the file: " + e.getMessage());
                        q.handleQuery(activeUser);
                    }
                }
                else {
                    try (BufferedReader reader = new BufferedReader(new FileReader(tableValueFileName))) {

                        String line;
                        System.out.println("----------Result---------");
                        System.out.println("Columns: " + Arrays.asList(columnsToPrint));
                        while ((line = reader.readLine()) != null) {

                            String[] rowParts = line.split("\\s*#\\s*");

                            if (condition1CaseCheck(operator1, indexOfColumnToCompare1, valueStringToCompare1, rowParts)) {
                                List<String> resultArray = new ArrayList<>();

                                for (int i = 0; i < indexOfColumnsToPrint.size(); i++) {
                                    resultArray.add(rowParts[indexOfColumnsToPrint.get(i)]);
                                }
                                System.out.println("Row: " + resultArray);
                            }
                        }
                        LogManager.log("Query Executed Successfully",activeUser.getEmail());
                    } catch (IOException e) {
                        System.out.println("An error occurred while reading the file: " + e.getMessage());
                        q.handleQuery(activeUser);
                    }
                }
            }
            else {
                System.out.println("Unsupported operation! ");
            }
        } else {
            System.out.println("Table Does not exist");
        }
    }

    /**
     * checks if where condition is true for that particular row.
     *
     * @param operator used in the where condition
     * @param indexOfColumnToCompare2 in AND clause
     * @param valueStringToCompare2 in AND clause
     * @param rowParts array of values in that row
     * @return boolean if row passes the where condition
     */

    public boolean condition2CaseCheck(String operator, int indexOfColumnToCompare2, String valueStringToCompare2, String[] rowParts) {

        boolean conditionSatisfied = false;
        //System.out.println(operator +":"+ indexOfColumnToCompare1 +":"+ valueStringToCompare1);
        switch (operator) {

            case "=":
                //System.out.println("$$$$ "+rowParts[indexOfColumnToCompare2]);
                //System.out.println("value "+valueStringToCompare2);
                conditionSatisfied = rowParts[indexOfColumnToCompare2].equalsIgnoreCase(valueStringToCompare2) ? true : false;
                //System.out.println("********** "+conditionSatisfied);
                break;

            case "!=":
                conditionSatisfied = rowParts[indexOfColumnToCompare2].equalsIgnoreCase(valueStringToCompare2) ? false : true;
                break;

            case "<":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare2])<Integer.parseInt(valueStringToCompare2) ? true : false;
                break;

            case ">":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare2])>Integer.parseInt(valueStringToCompare2) ? true : false;
                break;

            case ">=":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare2])>=Integer.parseInt(valueStringToCompare2) ? true : false;
                break;

            case "<=":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare2])<=Integer.parseInt(valueStringToCompare2) ? true : false;
                break;
            default:
                System.out.println("unsupported operator:"+operator);

        }
        return conditionSatisfied;
    }

    /**
     * checks if where condition is true for that particular row.
     *
     * @param operator used in the where condition
     * @param indexOfColumnToCompare1 in where clause
     * @param valueStringToCompare1 in where clause
     * @param rowParts array of values in that row
     * @return boolean if row passes the where condition
     */

    public boolean condition1CaseCheck(String operator, int indexOfColumnToCompare1, String valueStringToCompare1, String[] rowParts) {

        boolean conditionSatisfied = false;
        //System.out.println(operator +":"+ indexOfColumnToCompare1 +":"+ valueStringToCompare1);
        switch (operator) {

            case "=":
                conditionSatisfied = rowParts[indexOfColumnToCompare1].equalsIgnoreCase(valueStringToCompare1) ? true : false;
                //System.out.println("********** "+conditionSatisfied);
                break;

            case "!=":
                conditionSatisfied = rowParts[indexOfColumnToCompare1].equalsIgnoreCase(valueStringToCompare1) ? false : true;
                break;

            case "<":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare1])<Integer.parseInt(valueStringToCompare1) ? true : false;
                break;

            case ">":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare1])>Integer.parseInt(valueStringToCompare1) ? true : false;
                break;

            case ">=":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare1])>=Integer.parseInt(valueStringToCompare1) ? true : false;
                break;

            case "<=":
                conditionSatisfied = Integer.parseInt(rowParts[indexOfColumnToCompare1])<=Integer.parseInt(valueStringToCompare1) ? true : false;
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
