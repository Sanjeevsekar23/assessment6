package com.example.employeeaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EligibilityResult {

    private final Employee employee;
    private final AccessLevel requestedAccessLevel;
    private final EligibilityStatus status;
    private final List<String> reasons;

    public EligibilityResult(
            Employee employee,
            AccessLevel requestedAccessLevel,
            EligibilityStatus status,
            List<String> reasons) {

        this.employee = employee;
        this.requestedAccessLevel = requestedAccessLevel;
        this.status = status;
        this.reasons = Collections.unmodifiableList(
                new ArrayList<>(reasons));
    }

    public Employee getEmployee() {
        return employee;
    }

    public AccessLevel getRequestedAccessLevel() {
        return requestedAccessLevel;
    }

    public EligibilityStatus getStatus() {
        return status;
    }

    public List<String> getReasons() {
        return reasons;
    }
}