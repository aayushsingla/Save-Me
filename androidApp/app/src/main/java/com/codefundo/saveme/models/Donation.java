package com.codefundo.saveme.models;

public class Donation implements Comparable<Donation> {

    private String id;          //any Random unique Id
    private String name;      // "name"
    private int amount;     // "Medical Help" or "Food Camp" or we will think of this
    private String phoneNumber, email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int compareTo(Donation o) {
        return Integer.compare(amount, o.getAmount());
    }
}
