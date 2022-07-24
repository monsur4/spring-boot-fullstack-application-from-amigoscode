package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Expected result is written according to requirements.
 * Actual result is which we get when we execute the test cases
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    // since we have tested the student repository and know that it actually works, therefore we can rely on its
    // implementation and assume that it works fine
    @Mock private StudentRepository studentRepository;
//    private AutoCloseable autoCloseable;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this); // creates a mock of all the objects annotated as mock
        underTest = new StudentService(studentRepository);
    }
/*
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }*/

    @Test
    void canGetAllStudents() {
        // because we know that the studentRepository class works fine, what the mocking does is to actually confirm if
        // the appropriate method is being called

        // when
        underTest.getAllStudents();

        // then
        verify(studentRepository).findAll(); // verify that the findAll method of the object is called
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student("Monsuru", "okuniyimonsuru@yahoo.com", Gender.MALE);

        // when
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedValue = studentArgumentCaptor.getValue();

        assertThat(capturedValue).isEqualTo(student);

    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student("Monsuru", "okuniyimonsuru@yahoo.com", Gender.MALE);

        // by default studentRepository.selectExistsEmail(student.getEmail()) will return false,
        // but we need it to return true, so we do this below
        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);
        // when
        // then
        assertThatThrownBy(()->underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        // given
        Long studentId = 50L;
        given(studentRepository.existsById(anyLong()))
                .willReturn(true);
        // when
        underTest.deleteStudent(studentId);

        // then
        ArgumentCaptor<Long> studentIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(studentRepository).deleteById(studentIdArgumentCaptor.capture());

        Long capturedValue = studentIdArgumentCaptor.getValue();

        assertThat(capturedValue).isEqualTo(studentId);
    }

    @Test
    void willThrowExceptionWhenIdDoesNotExist() {
        // given
        Long studentId = 50L;
        given(studentRepository.existsById(anyLong()))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }
}