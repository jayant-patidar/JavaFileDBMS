package com.dal.filedbms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;



/**
 * A  class for handling user login
 */

public class UserLoginManager {

    UserController uc = new UserController();

    private static final String USER_FILE = "users.txt";

    Scanner sc = new Scanner(System.in);
    User currentUser = new User();

    /**
     * validates login credentials from user file
     *
     * @throws IOException If there was an error writing to the file.
     */

    public User validateLogin() {
        boolean valid = false;
        System.out.print("\nLogin >  Enter Email : ");
        String enteredEmail = sc.nextLine().toLowerCase();
        User currentUser = findUserByEmail(enteredEmail);
        if (currentUser.getEmail() != null) {

            System.out.print("\nEnter Password : ");
            String enteredRawPassword = sc.nextLine();

            if (validatePassword(enteredRawPassword, currentUser.getPasswordHash())) {

                System.out.print("\nPassword Matched");
                System.out.print("\nSecurity Question : " + currentUser.getSecurityQuestion() + ": ");
                if (validateSecurityQuestion()) {
                    System.out.println("--Correct Answer--");
                    valid = true;
                    LogManager.log(currentUser.getEmail() + " has successfully logged in. ", currentUser.getEmail());
                    return currentUser;
                } else {
                    System.out.println("--Incorrect Answer--");
                }
            } else {
                System.out.println("Incorrect Password");
            }
        } else {
            System.out.println("User Not Found");
        }
        return null;
    }

    private String convertHashToString(byte[] hashedPasswordToConvert) {
        StringBuilder hexSB = new StringBuilder();
        for (byte b : hashedPasswordToConvert) {
            hexSB.append(String.format("%02x", b & 0xff));
        }
        return hexSB.toString();
    }

    private byte[] hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not hash password"+ e.getMessage());
            uc.manageUser();
            return null;
        }
    }

    private boolean validateSecurityQuestion() {

        if (sc.nextLine().equalsIgnoreCase(currentUser.getSecurityAnswer())) {
            return true;

        } else {
            return false;
        }
    }

    public boolean validatePassword(String enteredRawPassword, String foundPassword) {

        return foundPassword.equals(convertHashToString(hashPassword(enteredRawPassword)));

    }

    public User findUserByEmail(String tempEmail) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("%");
                String email = parts[0];
                if (email.equals(tempEmail)) {
                    System.out.println("User Found! - Hello " + email);

                    currentUser.setEmail(parts[0]);
                    currentUser.setPasswordHash(parts[1]);
                    currentUser.setSecurityQuestion(parts[2]);
                    currentUser.setSecurityAnswer(parts[3]);
                    break;
                } else {
                    continue;
                }
            }
            return currentUser;
        } catch (IOException e) {
            System.out.println("An error occurred while reading the user file: " + e.getMessage());
            uc.manageUser();
        }

        return currentUser;
    }

}
