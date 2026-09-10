package com.example.employeeaccess;

import java.util.Objects;

public final class Employee {

    private final String employeeId;
    private final String name;
    private final int age;
    private final String department;
    private final EmploymentType employmentType;
    private final EmploymentStatus employmentStatus;
    private final SecurityClearance securityClearance;
    private final boolean idValid;

    public Employee(
            String employeeId,
            String name,
            int age,
            String department,
            EmploymentType employmentType,
            EmploymentStatus employmentStatus,
            SecurityClearance securityClearance,
            boolean idValid) {

        this.employeeId = validateText(employeeId, "Employee ID");
        this.name = validateText(name, "Name");

        if (age < 0 || age > 120) {
            throw new IllegalArgumentException(
                    "Age must be between 0 and 120");
        }

        this.age = age;
        this.department = validateText(department, "Department");

        this.employmentType = Objects.requireNonNull(
                employmentType,
                "Employment type is required");

        this.employmentStatus = Objects.requireNonNull(
                employmentStatus,
                "Employment status is required");

        this.securityClearance = Objects.requireNonNull(
                securityClearance,
                "Security clearance is required");

        this.idValid = idValid;
    }

    private static String validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }

        return value.trim();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDepartment() {
        return department;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public SecurityClearance getSecurityClearance() {
        return securityClearance;
    }

    public boolean isIdValid() {
        return idValid;
    }
}