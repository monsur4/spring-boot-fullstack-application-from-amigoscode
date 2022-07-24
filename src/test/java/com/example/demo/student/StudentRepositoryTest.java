package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * The only methods that should be tested are the methods that we write
 * and not the methods provided for us by the framework, because those have definitely been tested
 */
@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckWhenStudentEmailExists() {
        // given
        String email = "okuniyimonsuru@yahoo.com";
        Student student = new Student("Monsuru", email, Gender.MALE);
        underTest.save(student);

        // when
        boolean actual = underTest.selectExistsEmail(email);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void itShouldCheckWhenStudentEmailDoesNotExists() {
        // given
        String email = "okuniyimonsuru@yahoo.com";

        // when
        boolean actual = underTest.selectExistsEmail(email);

        // then
        assertThat(actual).isFalse();
    }
}