package com.example.employeeaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class EmployeeAccessService {

    private static final int MINIMUM_AGE = 21;

    private static final Set<String> AUTHORIZED_DEPARTMENTS =
            Set.of("IT", "HR", "FINANCE", "ADMINISTRATION");

    public EligibilityResult assess(
            Employee employee,
            AccessLevel requestedAccessLevel) {

        Objects.requireNonNull(employee, "Employee is required");
        Objects.requireNonNull(
                requestedAccessLevel,
                "Requested access level is required");

        List<String> rejectionReasons = new ArrayList<>();

        if (employee.getAge() < MINIMUM_AGE) {
            rejectionReasons.add(
                    "Employee must be at least 21 years old");
        }

        String department = employee.getDepartment()
                .toUpperCase(Locale.ROOT);

        if (!AUTHORIZED_DEPARTMENTS.contains(department)) {
            rejectionReasons.add("Department is not authorized");
        }

        if (employee.getEmploymentStatus()
                != EmploymentStatus.ACTIVE) {
            rejectionReasons.add(
                    "Employment status is not active");
        }

        if (!employee.isIdValid()) {
            rejectionReasons.add("Employee ID is invalid");
        }

        /*
         * Mandatory failures take priority.
         * All applicable reasons are returned together.
         */
        if (!rejectionReasons.isEmpty()) {
            return new EligibilityResult(
                    employee,
                    requestedAccessLevel,
                    EligibilityStatus.NOT_ELIGIBLE,
                    rejectionReasons);
        }

        /*
         * The employee passes mandatory checks but does not have
         * enough clearance for the requested resource.
         */
        if (!employee.getSecurityClearance()
                .meets(requestedAccessLevel)) {

            return new EligibilityResult(
                    employee,
                    requestedAccessLevel,
                    EligibilityStatus.CONDITIONALLY_ELIGIBLE,
                    List.of(
                            "Security clearance does not meet "
                                    + "the requested access level"));
        }

        return new EligibilityResult(
                employee,
                requestedAccessLevel,
                EligibilityStatus.ELIGIBLE,
                List.of());
    }
}