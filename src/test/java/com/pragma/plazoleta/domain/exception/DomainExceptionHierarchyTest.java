package com.pragma.plazoleta.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DomainExceptionHierarchyTest {

    private static final String MESSAGE = "Business error message";

    static Stream<Arguments> exceptionCategories() {
        return Stream.of(
                Arguments.of(new DishNotFoundException(MESSAGE), ResourceNotFoundException.class),
                Arguments.of(new OrderNotFoundException(MESSAGE), ResourceNotFoundException.class),
                Arguments.of(new RestaurantNotFoundException(MESSAGE), ResourceNotFoundException.class),

                Arguments.of(new UserNotOwnerOfRestaurantException(MESSAGE), ForbiddenOperationException.class),
                Arguments.of(new OrderDoesNotBelongToClientException(MESSAGE), ForbiddenOperationException.class),
                Arguments.of(new OrderDoesNotBelongToRestaurantException(MESSAGE), ForbiddenOperationException.class),
                Arguments.of(new EmployeeNotAssignedToRestaurantException(MESSAGE), ForbiddenOperationException.class),

                Arguments.of(new ClientHasActiveOrderException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new DishDoesNotBelongToRestaurantException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new DishInactiveException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new EmployeeAlreadyAssignedException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new InvalidDishDataException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new InvalidEmployeeException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new InvalidOrderDataException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new InvalidOrderStatusException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new InvalidRestaurantDataException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new InvalidSecurityPinException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new OrderCannotBeCancelledException(MESSAGE), BusinessRuleViolationException.class),
                Arguments.of(new OwnerNotValidException(MESSAGE), BusinessRuleViolationException.class),

                Arguments.of(new UserNotAuthenticatedException(MESSAGE), DomainException.class)
        );
    }

    @ParameterizedTest(name = "{0} should belong to {1}")
    @MethodSource("exceptionCategories")
    @DisplayName("Each domain exception should belong to its business category and keep its message")
    void shouldBelongToExpectedCategory(DomainException exception, Class<? extends DomainException> expectedCategory) {
        assertInstanceOf(expectedCategory, exception);
        assertInstanceOf(DomainException.class, exception);
        assertEquals(MESSAGE, exception.getMessage());
    }
}
