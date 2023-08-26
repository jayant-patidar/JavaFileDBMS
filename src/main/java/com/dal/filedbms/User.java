package com.dal.filedbms;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * A  model class for User
 */

public class User {
    private String email;
    private String passwordHash;
    private String securityQuestion;
    private String securityAnswer;

    public User() {
    }

    public User(String email, String password, String securityQuestion, String securityAnswer) {
        this.email = email;

        StringBuilder hexSB = new StringBuilder();
        byte[] hashedPassword = hashPassword(password);
        for (byte b : hashedPassword) {
            hexSB.append(String.format("%02x", b & 0xff));
        }
        String passwordHashHex = hexSB.toString();

        this.passwordHash = hexSB.toString();

        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    /**
     * creates new tables when user enter create table query.
     *
     * @param password to be encrypted.
     * @throws NoSuchAlgorithmException if algorithm not found.
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}