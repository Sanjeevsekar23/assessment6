package com.example.employeeaccess;

public class Main {

    public static void main(String[] args) {

        EligibilityService service = new EligibilityService();

        Employee employee1 = new Employee(
                "E101",
                "Arun",
                25,
                "IT",
                EmploymentType.FULL_TIME,
                EmploymentStatus.ACTIVE,
                SecurityClearance.SECRET,
                true
        );

        Employee employee2 = new Employee(
                "E102",
                "Bala",
                30,
                "HR",
                EmploymentType.FULL_TIME,
                EmploymentStatus.ACTIVE,
                SecurityClearance.BASIC,
                true
        );

        Employee employee3 = new Employee(
                "E103",
                "Charan",
                19,
                "SALES",
                EmploymentType.CONTRACT,
                EmploymentStatus.INACTIVE,
                SecurityClearance.NONE,
                false
        );

        displayResult(service.assess(employee1, AccessLevel.CONFIDENTIAL));
        displayResult(service.assess(employee2, AccessLevel.SECRET));
        displayResult(service.assess(employee3, AccessLevel.SECRET));
    }

    private static void displayResult(EligibilityResult result) {

        System.out.println("----------------------------------");
        System.out.println("Employee ID: "
                + result.getEmployee().getEmployeeId());
        System.out.println("Name: "
                + result.getEmployee().getName());
        System.out.println("Access Level: "
                + result.getRequestedAccessLevel());
        System.out.println("Status: "
                + result.getStatus());

        if (result.getReasons().isEmpty()) {
            System.out.println("Reasons: None");
        } else {
            System.out.println("Reasons:");
            for (String reason : result.getReasons()) {
                System.out.println("- " + reason);
            }
        }
    }
}