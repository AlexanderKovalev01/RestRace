package org.sk.races.rest.entities;

public class Runner {

    private String name;
    private int age;
    private String country;
    private Gender gender;

    public Runner( String name, int age, String country, Gender gender) {
        this.name = name;
        this.age = age;
        this.country = country;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getCountry() {
        return country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }
}