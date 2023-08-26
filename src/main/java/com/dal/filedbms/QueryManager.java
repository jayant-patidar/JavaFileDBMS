package com.dal.filedbms;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 * A  class for managing query entered  and routing to corresponding methods
 */


public class QueryManager {

    Scanner sc = new Scanner(System.in);

    /**
     * handles coming queries and call proper methods
     *
     * @param activeUser logged in user.
     * @throws IOException If there was an error writing to the file.
     */

    public void handleQuery(User activeUser) {


        System.out.println("------------Query Management------------");
        System.out.println("\n" + activeUser.getEmail() + "> Enter  Query :  ");
        System.out.print("\n" + activeUser.getEmail() + ">");

        String queryEntered = sc.nextLine();
        LogManager.log("Entered query : " + queryEntered, activeUser.getEmail());




        if (queryEntered.toUpperCase().contains("CREATE TABLE")) {


            CreateQueryExecuter createQueryExecuter = new CreateQueryExecuter();
            createQueryExecuter.executeCreateTableQuery(queryEntered,activeUser);

        }


        else if (queryEntered.toUpperCase().contains("INSERT INTO")) {

            InsertQueryExecuter insertQueryExecuter = new InsertQueryExecuter();
            boolean tableExist = insertQueryExecuter.executeInsertQuery(queryEntered,activeUser);
            if (tableExist){
                   System.out.println("Insert Done!");
            }else {
                handleQuery(activeUser);
            }
        }


        else if (queryEntered.toUpperCase().contains("SELECT")) {

            SelectQueryExecuter selectQueryExecuter = new SelectQueryExecuter();
            selectQueryExecuter.executeSelectQuery(queryEntered,activeUser);

        }


        else if (queryEntered.toUpperCase().contains("DELETE")) {

            DeleteQueryExecuter deleteQueryExecuter = new DeleteQueryExecuter();
            deleteQueryExecuter.executeDeleteQuery(queryEntered,activeUser);

        }


        else if(queryEntered.toUpperCase().contains("UPDATE")) {
            UpdateQueryExecuter updateQueryExecuter = new UpdateQueryExecuter();
            updateQueryExecuter.executeUpdateQuery(queryEntered,activeUser);
        }
    }

}
