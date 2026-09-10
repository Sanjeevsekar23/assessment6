package com.example.employeeaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EligibilityServiceTest {

    private final EligibilityService service =
            new EligibilityService();

    @Test
    void eligibleEmployeeHasNoReasons() {
        EligibilityResult result = service.assess(
                employee(21, "IT", EmploymentStatus.ACTIVE,
                        true, SecurityClearance.SECRET),
                AccessLevel.CONFIDENTIAL);

        assertEquals(EligibilityStatus.ELIGIBLE, result.getStatus());
        assertTrue(result.getReasons().isEmpty());
    }

    @Test
    void lowerClearanceIsConditionallyEligible() {
        EligibilityResult result = service.assess(
                employee(35, "Finance", EmploymentStatus.ACTIVE,
                        true, SecurityClearance.CONFIDENTIAL),
                AccessLevel.SECRET);

        assertEquals(
                EligibilityStatus.CONDITIONALLY_ELIGIBLE,
                result.getStatus());

        assertEquals(1, result.getReasons().size());
    }

    @Test
    void exactlyTwentyOneIsEligible() {
        EligibilityResult result = service.assess(
                employee(21, "HR", EmploymentStatus.ACTIVE,
                        true, SecurityClearance.BASIC),
                AccessLevel.INTERNAL);

        assertEquals(EligibilityStatus.ELIGIBLE, result.getStatus());
    }

    @ParameterizedTest
    @MethodSource("authorizedDepartments")
    void authorizedDepartmentsAreAccepted(String department) {
        EligibilityResult result = service.assess(
                employee(30, department, EmploymentStatus.ACTIVE,
                        true, SecurityClearance.BASIC),
                AccessLevel.INTERNAL);

        assertEquals(EligibilityStatus.ELIGIBLE, result.getStatus());
    }

    static Stream<Arguments> authorizedDepartments() {
        return Stream.of(
                Arguments.of("IT"),
                Arguments.of("HR"),
                Arguments.of("Finance"),
                Arguments.of("Administration"),
                Arguments.of("it"),
                Arguments.of("finance"));
    }

    @ParameterizedTest
    @MethodSource("mandatoryFailures")
    void mandatoryFailuresAreReported(
            int age,
            String department,
            EmploymentStatus status,
            boolean idValid,
            String reason) {

        EligibilityResult result = service.assess(
                employee(age, department, status,
                        idValid, SecurityClearance.TOP_SECRET),
                AccessLevel.PUBLIC);

        assertEquals(
                EligibilityStatus.NOT_ELIGIBLE,
                result.getStatus());

        assertTrue(result.getReasons().contains(reason));
    }

    static Stream<Arguments> mandatoryFailures() {
        return Stream.of(
                Arguments.of(
                        20,
                        "IT",
                        EmploymentStatus.ACTIVE,
                        true,
                        "Employee must be at least 21 years old"),

                Arguments.of(
                        30,
                        "Sales",
                        EmploymentStatus.ACTIVE,
                        true,
                        "Department is not authorized"),

                Arguments.of(
                        30,
                        "IT",
                        EmploymentStatus.INACTIVE,
                        true,
                        "Employment status is not active"),

                Arguments.of(
                        30,
                        "IT",
                        EmploymentStatus.SUSPENDED,
                        true,
                        "Employment status is not active"),

                Arguments.of(
                        30,
                        "IT",
                        EmploymentStatus.ACTIVE,
                        false,
                        "Employee ID is invalid"));
    }

    @Test
    void multipleFailuresAreReportedTogether() {
        EligibilityResult result = service.assess(
                employee(20, "Sales",
                        EmploymentStatus.SUSPENDED,
                        false,
                        SecurityClearance.NONE),
                AccessLevel.TOP_SECRET);

        assertEquals(
                EligibilityStatus.NOT_ELIGIBLE,
                result.getStatus());

        assertEquals(4, result.getReasons().size());

        assertTrue(result.getReasons().contains(
                "Employee must be at least 21 years old"));

        assertTrue(result.getReasons().contains(
                "Department is not authorized"));

        assertTrue(result.getReasons().contains(
                "Employment status is not active"));

        assertTrue(result.getReasons().contains(
                "Employee ID is invalid"));
    }

    @ParameterizedTest
    @MethodSource("clearanceCases")
    void clearanceDeterminesAccess(
            SecurityClearance clearance,
            AccessLevel access,
            EligibilityStatus expected) {

        EligibilityResult result = service.assess(
                employee(30, "HR", EmploymentStatus.ACTIVE,
                        true, clearance),
                access);

        assertEquals(expected, result.getStatus());
    }

    static Stream<Arguments> clearanceCases() {
        return Stream.of(
                Arguments.of(
                        SecurityClearance.NONE,
                        AccessLevel.PUBLIC,
                        EligibilityStatus.ELIGIBLE),

                Arguments.of(
                        SecurityClearance.BASIC,
                        AccessLevel.CONFIDENTIAL,
                        EligibilityStatus.CONDITIONALLY_ELIGIBLE),

                Arguments.of(
                        SecurityClearance.CONFIDENTIAL,
                        AccessLevel.SECRET,
                        EligibilityStatus.CONDITIONALLY_ELIGIBLE),

                Arguments.of(
                        SecurityClearance.TOP_SECRET,
                        AccessLevel.TOP_SECRET,
                        EligibilityStatus.ELIGIBLE));
    }

    @ParameterizedTest
    @MethodSource("clearanceBoundaryCases")
    void clearanceBoundaryIsInclusive(
            SecurityClearance clearance,
            AccessLevel access) {

        EligibilityResult result = service.assess(
                employee(30, "IT", EmploymentStatus.ACTIVE,
                        true, clearance),
                access);

        assertEquals(
                EligibilityStatus.ELIGIBLE,
                result.getStatus());
    }

    static Stream<Arguments> clearanceBoundaryCases() {
        return Stream.of(
                Arguments.of(
                        SecurityClearance.NONE,
                        AccessLevel.PUBLIC),

                Arguments.of(
                        SecurityClearance.BASIC,
                        AccessLevel.INTERNAL),

                Arguments.of(
                        SecurityClearance.CONFIDENTIAL,
                        AccessLevel.CONFIDENTIAL),

                Arguments.of(
                        SecurityClearance.SECRET,
                        AccessLevel.SECRET),

                Arguments.of(
                        SecurityClearance.TOP_SECRET,
                        AccessLevel.TOP_SECRET));
    }

    @Test
    void multipleEmployeesAreAssessedIndependently() {
        List<Employee> employees = List.of(
                employee(30, "IT",
                        EmploymentStatus.ACTIVE,
                        true,
                        SecurityClearance.SECRET),

                employee(20, "Sales",
                        EmploymentStatus.INACTIVE,
                        false,
                        SecurityClearance.NONE));

        List<EligibilityStatus> statuses = employees.stream()
                .map(employee -> service.assess(
                        employee,
                        AccessLevel.CONFIDENTIAL)
                        .getStatus())
                .toList();

        assertEquals(
                List.of(
                        EligibilityStatus.ELIGIBLE,
                        EligibilityStatus.NOT_ELIGIBLE),
                statuses);
    }

    @Test
    void nullEmployeeIsRejected() {
        assertThrows(
                NullPointerException.class,
                () -> service.assess(
                        null,
                        AccessLevel.PUBLIC));
    }

    @Test
    void nullAccessLevelIsRejected() {
        Employee employee = employee(
                30,
                "IT",
                EmploymentStatus.ACTIVE,
                true,
                SecurityClearance.BASIC);

        assertThrows(
                NullPointerException.class,
                () -> service.assess(employee, null));
    }

    @Test
    void invalidAgeIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> employee(
                        121,
                        "IT",
                        EmploymentStatus.ACTIVE,
                        true,
                        SecurityClearance.BASIC));

        assertThrows(
                IllegalArgumentException.class,
                () -> employee(
                        -1,
                        "IT",
                        EmploymentStatus.ACTIVE,
                        true,
                        SecurityClearance.BASIC));
    }

    @Test
    void blankEmployeeFieldsAreRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        "",
                        "Name",
                        30,
                        "IT",
                        EmploymentType.FULL_TIME,
                        EmploymentStatus.ACTIVE,
                        SecurityClearance.BASIC,
                        true));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        "E1",
                        "",
                        30,
                        "IT",
                        EmploymentType.FULL_TIME,
                        EmploymentStatus.ACTIVE,
                        SecurityClearance.BASIC,
                        true));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        "E1",
                        "Name",
                        30,
                        "",
                        EmploymentType.FULL_TIME,
                        EmploymentStatus.ACTIVE,
                        SecurityClearance.BASIC,
                        true));
    }

    @Test
    void nullEmployeeFieldsAreRejected() {
        assertThrows(
                NullPointerException.class,
                () -> new Employee(
                        "E1",
                        "Name",
                        30,
                        "IT",
                        null,
                        EmploymentStatus.ACTIVE,
                        SecurityClearance.BASIC,
                        true));

        assertThrows(
                NullPointerException.class,
                () -> new Employee(
                        "E1",
                        "Name",
                        30,
                        "IT",
                        EmploymentType.FULL_TIME,
                        null,
                        SecurityClearance.BASIC,
                        true));

        assertThrows(
                NullPointerException.class,
                () -> new Employee(
                        "E1",
                        "Name",
                        30,
                        "IT",
                        EmploymentType.FULL_TIME,
                        EmploymentStatus.ACTIVE,
                        null,
                        true));
    }

    @Test
    void reasonsAreImmutable() {
        EligibilityResult result = service.assess(
                employee(20, "Sales",
                        EmploymentStatus.INACTIVE,
                        false,
                        SecurityClearance.NONE),
                AccessLevel.PUBLIC);

        assertThrows(
                UnsupportedOperationException.class,
                () -> result.getReasons().add("changed"));

        assertEquals(
                List.of(
                        "Employee must be at least 21 years old",
                        "Department is not authorized",
                        "Employment status is not active",
                        "Employee ID is invalid"),
                result.getReasons());
    }

    private static Employee employee(
            int age,
            String department,
            EmploymentStatus status,
            boolean idValid,
            SecurityClearance clearance) {

        return new Employee(
                "EMP-1001",
                "Test Employee",
                age,
                department,
                EmploymentType.FULL_TIME,
                status,
                clearance,
                idValid);
    }
}