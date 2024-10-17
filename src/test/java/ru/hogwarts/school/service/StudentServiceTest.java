package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.impl.StudentServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles(value = "test")
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repositoryMock;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl service;

    @Test
    void shouldAddStudentTest() {
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Student");
        student1.setAge(18);
        when(repositoryMock.save(student1)).thenReturn(student1);

        Student student2 = new Student();
        student2.setId(1L);
        student2.setName("Student");
        student2.setAge(18);

        FacultyDtoOut facultyDtoOut = new FacultyDtoOut();

        StudentDtoIn studentDtoIn = new StudentDtoIn("Student", 18, 1L);
        StudentDtoOut studentDtoOut = new StudentDtoOut();

        when(studentMapper.toDto(student1)).thenReturn(studentDtoOut);
        when(studentMapper.toEntity(studentDtoIn)).thenReturn(student2);

        assertThat(studentDtoOut).isSameAs(service.create(studentDtoIn));

        verify(repositoryMock).save(student1);
        verify(studentMapper).toDto(student1);
        verify(studentMapper).toEntity(studentDtoIn);
        verify(repositoryMock, times(1)).save(student1);
        verify(repositoryMock, times(1)).save(student2);
    }

    @Test
    void shouldAddStudentNegativeTest() {
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Student");
        student1.setAge(14);

        StudentDtoIn studentDtoIn = new StudentDtoIn("Student", 14, 1L);

        when(studentMapper.toEntity(studentDtoIn))
                .thenThrow(new StudentNotFoundException(1L));
        assertThrows(StudentNotFoundException.class,
                () -> service.create(studentDtoIn));

        verify(studentMapper).toEntity(studentDtoIn);
        verify(repositoryMock, never()).save(student1);
    }

    @Test
    void getTest() {
        Student student = new Student();
        student.setId(1L);
        student.setName("Name");
        student.setAge(11);

        Optional<Student> result = Optional.of(student);
        when(repositoryMock.findById(any())).thenReturn(result);

        StudentDtoOut studentDtoOut = new StudentDtoOut();
        when(studentMapper.toDto(any())).thenReturn(studentDtoOut);

        assertThat(studentDtoOut)
                .isSameAs(service.get(any()));

        verify(repositoryMock).findById(any());
        verify(studentMapper).toDto(any());
    }

}