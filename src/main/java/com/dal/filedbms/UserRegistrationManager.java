package com.dal.filedbms;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


/**
 * A  class for handling new user registration
 */

public class UserRegistrationManager {
    private static final String USER_FILE = "users.txt";

    private static final String LOG_FILE = "logs.txt";

    Scanner sc = new Scanner(System.in);
    UserController uc = new UserController();

    /**
     * stores new users to user file
     *
     * @throws IOException If there was an error writing to the file.
     */

    public void registerUser() {
        System.out.println("--- New User Register ---- \n");
        User newUser = new User();
        System.out.print("\nEnter  Email : ");
        newUser.setEmail(sc.nextLine().toLowerCase());
        LogManager.log("Entered Email - " + newUser.getEmail(), newUser.getEmail());

        if (!ifAlreadyExist(newUser.getEmail())) {
            System.out.print("\nEnter  Password : ");
            newUser.setPasswordHash(convertHashToString(hashPassword(sc.nextLine())));
            //System.out.print("\nHash of entered password is : " +newUser.getPasswordHash());
            System.out.print("\nEnter Security Question : ");
            newUser.setSecurityQuestion(sc.nextLine());
            System.out.print("\nEnter Security Answer : ");
            newUser.setSecurityAnswer(sc.nextLine());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
                String line = newUser.getEmail() + "%" + newUser.getPasswordHash() + "%" + newUser.getSecurityQuestion() + "%" + newUser.getSecurityAnswer();
                writer.write(line);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Error writing to user file: " + e.getMessage());
                uc.manageUser();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                System.out.println("\nRegistration Successful");
                LogManager.log("Registration Successfully Completed by - " + newUser.getEmail(), newUser.getEmail());


            } catch (IOException e) {
                System.out.println("Error writing to Log file: " + e.getMessage());
                uc.manageUser();
            }

        } else {
            System.out.println("User Already Exists!");
            registerUser();
        }

    }
    /**
     * to convert password to string for saving in file
     *
     * @param hashedPasswordToConvert hashed password
     * @throws IOException If there was an error writing to the file.
     */

    private String convertHashToString(byte[] hashedPasswordToConvert) {
        StringBuilder hexSB = new StringBuilder();
        for (byte b : hashedPasswordToConvert) {
            hexSB.append(String.format("%02x", b & 0xff));
        }
        return hexSB.toString();
    }

    /**
     * to encrypting password
     *
     * @param password  password to be hashed
     * @throws IOException If there was an error writing to the file.
     */
    private byte[] hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }


    /**
     * check if user already exists
     *
     * @param email user email
     * @throws IOException If there was an error writing to the file.
     */
    public boolean ifAlreadyExist(String email) {
        boolean userAlreadyExist = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("%");

                if (email.equals(parts[0])) {
                    userAlreadyExist = true;
                } else {
                    continue;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the user file: " + e.getMessage());
            uc.manageUser();
        }
        return userAlreadyExist;
    }
    /** references :
     https://www.w3schools.com/sql/sql_join.asp
     https://www.w3schools.com/java/java_files.asp
     https://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html

     */
}
