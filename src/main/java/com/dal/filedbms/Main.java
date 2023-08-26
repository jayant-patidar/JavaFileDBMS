package com.dal.filedbms;

import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * A  class for starting the application
 */

public class Main {
    public static void main(String[] args) {

        UserController userController = new UserController();



        userController.manageUser();

    }
}