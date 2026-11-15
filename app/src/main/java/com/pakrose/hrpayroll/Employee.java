
package com.pakrose.hrpayroll;

public class Employee {

    private int id;
    private String name;
    private String phone;
    private String designation;
    private String joiningDate;
    private double basicSalary;
    private String photoPath;
    private boolean active;

    public Employee(
            int id,
            String name,
            String phone,
            String designation,
            String joiningDate,
            double basicSalary,
            String photoPath,
            boolean active) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.basicSalary = basicSalary;
        this.photoPath = photoPath;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
