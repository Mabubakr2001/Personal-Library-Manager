package dev.bakr.library_manager;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//@SpringBootTest
class LibraryManagerApplicationTests {
    Calculator calculator = new Calculator();

    @Test
    void shouldAddTwoNumbers() {
        // given
        int numberOne = 20;
        int numberTwo = 20;

        // when
        int actualResult = calculator.add(numberOne, numberTwo);

        // then
        int expectedResult = 40;
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Nested
    class Calculator {
        int add(int a, int b) {
            return a + b;
        }
    }
}
