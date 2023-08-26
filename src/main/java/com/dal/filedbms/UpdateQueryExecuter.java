package com.dal.filedbms;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A  class for executing update query to modify certain row
 */

public class UpdateQueryExecuter {

    QueryManager q = new QueryManager();
    /**
     * update data in row when user enter update query.
     *
     * @param queryEntered query to be executed.
     * @param activeUser current logged in user.
     * @throws IOException If there was an error writing to the file.
     */

    public void executeUpdateQuery(String queryEntered, User activeUser) {

        if (queryEntered.contains(";")) {
            queryEntered = queryEntered.replace(";", "");
        }
        queryEntered = queryEntered.toLowerCase();
        String tableName;
        boolean tableExist = false;

        String whereCondition = "";
        String columnToCompare = "";
        String operator = "";
        int indexOfColumnToCompare = 0;
        String valueStringToCompare = "";
        List<String> tempArray = new ArrayList<>();
        tableName = queryEntered.trim().substring((queryEntered.toLowerCase().indexOf("update") + 6), queryEntered.toLowerCase().indexOf("set")).trim();
        whereCondition = queryEntered.substring(queryEntered.toLowerCase().indexOf("where") + 5);

        System.out.println("Table name : " + tableName);
        System.out.println("Where condition :" + whereCondition);

        String tableNameToSearch = activeUser.getEmail() + "-" + tableName + ".jpd";
        String tableValueFileName = activeUser.getEmail() + "-" + tableName + "-content" + ".jpd";

        File tableToSearch = new File(tableNameToSearch);

        File valueTableToSearch = new File(tableValueFileName);
        String[] columnList={};

        try (BufferedReader br2 = new BufferedReader(new FileReader(tableNameToSearch))) {
            columnList = br2.readLine().trim().split("#");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
            q.handleQuery(activeUser);
        }

        String[] dataToUpdate = queryEntered.trim().substring((queryEntered.toLowerCase().indexOf("set") + 3),queryEntered.toLowerCase().indexOf("where")).split(",");
        System.out.println(Arrays.asList(dataToUpdate));
        String[] colsToUpdate = new  String[dataToUpdate.length];
        List<String> tempColsToUpdate = new ArrayList<>();
        String[] valueToUpdate = new  String[dataToUpdate.length];
        List<String> tempValuesToUpdate = new ArrayList<>();
        List<Integer> indexToUpdate = new ArrayList<>();


        for (int i = 0; i < dataToUpdate.length; i++) {

            if(dataToUpdate[i].contains(columnList[0].trim())){
                //System.out.println("contains id - ");
                tempColsToUpdate.add(dataToUpdate[i].trim().split("=")[0].trim());
                tempValuesToUpdate.add(dataToUpdate[i].trim().split("=")[1].trim());
                //System.out.println("contains id - "+tempColsToUpdate.get(i));
                //System.out.println("contains id - "+tempValuesToUpdate.get(i));
            } else if (dataToUpdate[i].contains(columnList[1].trim())){
                //System.out.println("contains name - ");
                tempColsToUpdate.add(dataToUpdate[i].trim().split("=")[0].trim());
                tempValuesToUpdate.add(dataToUpdate[i].trim().split("=")[1].trim());
                //System.out.println("contains name - "+tempColsToUpdate.get(i));
                //System.out.println("contains name - "+tempValuesToUpdate.get(i));
            }else if (dataToUpdate[i].contains(columnList[2].trim())){
                //System.out.println("contains email - ");
                tempColsToUpdate.add(dataToUpdate[i].trim().split("=")[0].trim());
                tempValuesToUpdate.add(dataToUpdate[i].trim().split("=")[1].trim());
                //System.out.println("contains email - "+tempColsToUpdate.get(i));
                //System.out.println("contains email - "+tempValuesToUpdate.get(i));
            }else if (dataToUpdate[i].contains(columnList[3].trim())){
                //System.out.println("contains address - ");
                tempColsToUpdate.add(dataToUpdate[i].trim().split("=")[0].trim());
                tempValuesToUpdate.add(dataToUpdate[i].trim().split("=")[1].trim());
                //System.out.println("contains address - "+tempColsToUpdate.get(i));
                //System.out.println("contains address - "+tempValuesToUpdate.get(i));
            }


        }

        for (int i = 0; i < tempColsToUpdate.size(); i++) {
            colsToUpdate[i] = tempColsToUpdate.get(i);
            valueToUpdate[i] = tempValuesToUpdate.get(i);
        }



        for (int i = 0; i < dataToUpdate.length; i++) {
            colsToUpdate[i] = colsToUpdate[i].replace("'","");
            //System.out.print("\n"+colsToUpdate[i]+"-");
            valueToUpdate[i]=valueToUpdate[i].replace("'","");
            //System.out.print(valueToUpdate[i]+"\n");
        }

        for (String c : colsToUpdate) {
            c = c.trim();
            try (BufferedReader br2 = new BufferedReader(new FileReader(tableNameToSearch))) {

                for (int i = 0; i < columnList.length; i++) {
                    columnList[i]=columnList[i].trim();
                    //System.out.println("attribute : "+ Arrays.asList(columnList));
                    if (c.trim().equalsIgnoreCase(columnList[i].trim())) {
                        indexToUpdate.add(i);
                    }
                }

            } catch (IOException e) {
                System.out.println("An error occurred while reading the file: " + e.getMessage());
                q.handleQuery(activeUser);
            }
        }

        //System.out.println(Arrays.asList("index to update"+indexToUpdate));
        //System.out.println(Arrays.asList(colsToUpdate));
        //System.out.println(Arrays.asList(valueToUpdate));

        if (tableToSearch.exists() && !tableToSearch.isDirectory()) {
            tableExist = true;
            System.out.println("Table found!");

            String[] whereConditionSplit = {};
            String[] operatorTypes = {"=", "!=", "<", ">", ">=", "<=", "between", "in", "like"};

            for (String s : operatorTypes) {
                if (whereCondition.contains(s)) {
                    whereConditionSplit = whereCondition.trim().split(s);
                    operator = s;
                    columnToCompare = whereConditionSplit[0].trim();
                    valueStringToCompare = whereConditionSplit[1].trim().replace("'","");

                }
            }

            System.out.println("operator matched: " + operator);
            System.out.println("value to compare: " + valueStringToCompare);
            System.out.println("column to compare: " + columnToCompare);

            try (BufferedReader br2 = new BufferedReader(new FileReader(tableNameToSearch))) {
                //columnList = br2.readLine().trim().split("\\s*#\\s*");
                //System.out.println(Arrays.asList(columnList));
                //System.out.println(columnToCompare);
                indexOfColumnToCompare = Arrays.binarySearch(columnList, columnToCompare.trim());
                System.out.println("index to compare: "+indexOfColumnToCompare);

            } catch (IOException e) {
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
                    else {

                        String newLine = "";
                        int n=0;
                        for (int i = 0; i < rowParts.length; i++) {
                            if (indexToUpdate.contains(i)){


                                newLine=newLine+""+valueToUpdate[n]+"#";
                                n++;
                                //System.out.println(newLine);
                            }else {
                                newLine=newLine+""+rowParts[i]+"#";
                                //System.out.println(newLine);
                            }
                        }
                        tempArray.add(newLine);
                        System.out.println("New Row: ["+newLine+"]");

                    }


                }

                LogManager.log("Query Executed Successfully",activeUser.getEmail());

            } catch (IOException e) {
                System.out.println("An error occurred while reading the file: " + e.getMessage());
                q.handleQuery(activeUser);
            }

            try  {
                FileWriter fw = new FileWriter(tableValueFileName,false);
                fw.write("");
            } catch (IOException e) {
                e.printStackTrace();
                q.handleQuery(activeUser);
            }



            try (FileWriter fw = new FileWriter(tableValueFileName)) {
                for (String line : tempArray) {
                    fw.write(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                q.handleQuery(activeUser);
            }





        }

    }

    /**
     * deletes row when user enter delete query.
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

