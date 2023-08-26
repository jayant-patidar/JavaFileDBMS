package com.dal.filedbms;

import java.io.IOException;
import java.util.Scanner;


/**
 * A  class for managing transactions
 */

public class TransactionManager {

    Scanner sc = new Scanner(System.in);
    private static boolean inTransaction = false;

    /**
     * starts new transaction
     *
     * @param activeUser current logged in user.
     */

    public void handleTransaction(User activeUser) {

        QueryManager queryManager = new QueryManager();
        System.out.println("------------Database Management------------");
        System.out.println("\n Enter  BEGIN or EXIT ");
        System.out.print("$" + activeUser.getEmail() + ">");
        switch (sc.nextLine().toUpperCase()) {
            case "BEGIN":

                System.out.println("You entered BEGIN");
                LogManager.log("Entered Begin",activeUser.getEmail());
                startTransaction();
                LogManager.log("Transaction Started",activeUser.getEmail());

                if (inTransaction) {
                    queryManager.handleQuery(activeUser);
                }
                handleTransaction(activeUser);
                break;
            case "EXIT":
                System.out.println("You entered EXIT");
                LogManager.log("User Exit",activeUser.getEmail());

                System.exit(0);
                break;
            default:
                System.out.println("Bad input!! ---- Try Again ----");
                LogManager.log("Entered Invalid Option",activeUser.getEmail());

                handleTransaction(activeUser);
                break;
        }


    }

    /**
     * starts transactions
     *
     *
     */
    private static void startTransaction() {
        if (!inTransaction) {
            inTransaction = true;
            System.out.println("Transaction started.");
        } else {
            System.out.println("Transaction already started.");
        }
    }

    /**
     * commits transactions
     *
     *
     */
    private static void commitTransaction() {
        if (inTransaction) {
            inTransaction = false;

        } else {
            System.out.println("Not in transaction. Use 'BEGIN' to start a transaction.");
        }
    }

    /**
     * rollbacks transactions
     *
     *
     */
    private static void rollbackTransaction() {
        if (inTransaction) {
            inTransaction = false;
        }
    }
}