package com.dal.filedbms;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * A  class for managing users and routing to appropriate method for registration or login
 */

public class UserController {

    /**
     * for hndling and routing the user for registration and login
     *
     * @throws InputMismatchException for wrong input by user
     */
    public void manageUser() {

        System.out.println("------------Welcome to FileDBMS----------- \n");
        System.out.println("Enter 1 or 2 :  \n 1. Login \n 2. Register \n0. Exit");
        try {
            Scanner sc = new Scanner(System.in);
            int n = sc.nextInt();

            switch (n) {
                case 1:
                    UserLoginManager loginManager = new UserLoginManager();
                    User activeUser;
                    if ((activeUser = loginManager.validateLogin()) != null) {

                        TransactionManager transactionManager = new TransactionManager();

                        transactionManager.handleTransaction(activeUser);

                    } else {
                        manageUser();
                    }
                    break;
                case 2:
                    UserRegistrationManager userRegistrationManager = new UserRegistrationManager();
                    userRegistrationManager.registerUser();
                    manageUser();
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Bad input!! ---- Try Again ----");
                    manageUser();
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Bad input!! ---- Try Again ----\n");
            manageUser();
        }
    }
}
