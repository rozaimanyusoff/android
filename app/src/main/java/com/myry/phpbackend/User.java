package com.myry.phpbackend;

public class User {

    private int id;
    private String username, email, fname, contact;

    public User() {
    }


    public User(int id, String username, String email, String fname, String contact) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fname = fname;
        this.contact = contact;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fname;
    }

    public void setFullname(String fname) {
        this.fname = fname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
