package ru.hogwarts.school.service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.mapper.AvatarMapper;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.impl.AvatarServiceImpl;
import ru.hogwarts.school.service.impl.StudentServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
class StudentControllerTest {

    private final Faker faker = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private StudentServiceImpl studentService;
    @SpyBean
    private AvatarServiceImpl avatarService;
    @SpyBean
    private FacultyMapper facultyMapper;
    @SpyBean
    private StudentMapper studentMapper;
    @SpyBean
    private AvatarMapper avatarMapper;
    @MockBean
    private FacultyRepository facultyRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private AvatarRepository avatarRepository;

    @Test
    void getStudentTest() throws Exception {
        Student student = generateStudent(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getName()).isEqualTo(student.getName());
                    assertThat(studentDtoOut.getAge()).isEqualTo(student.getAge());
                });
    }

    @Test
    void getStudentNotFoundTest() throws Exception {
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).contains("id = 2");
                });
    }

    @Test
    void updateStudentTest() throws Exception {
        StudentDtoIn studentDtoIn = generateDto();

        Faculty faculty = generateFaculty();

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        Student oldStudent = generateStudent(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(oldStudent));

        oldStudent.setName(studentDtoIn.getName());
        oldStudent.setAge(studentDtoIn.getAge());

        when(studentRepository.save(any(Student.class))).thenReturn(oldStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/1")
                        .content(objectMapper.writeValueAsString(studentDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getName()).isEqualTo(studentDtoIn.getName());
                    assertThat(studentDtoOut.getAge()).isEqualTo(studentDtoIn.getAge());
                });

        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void updateStudentNotFoundTest() throws Exception {
        StudentDtoIn studentDtoIn = generateDto();

        when(studentRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/2")
                        .content(objectMapper.writeValueAsString(studentDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).contains("id = 2");
                });

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void deleteStudentTest() throws Exception {
        Student student = generateStudent(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getName()).isEqualTo(student.getName());
                    assertThat(studentDtoOut.getAge()).isEqualTo(student.getAge());
                });

        verify(studentRepository, times(1)).delete(any(Student.class));
    }

    @Test
    void deleteStudentNotFoundTest() throws Exception {
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).contains("id = 2");
                });

        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void findAllStudentsTest() throws Exception {
        List<Student> students = Stream.iterate(1L, id -> id + 1)
                .map(this::generateStudent)
                .limit(20)
                .toList();

        List<StudentDtoOut> expectedResult = students.stream()
                .map(studentMapper::toDto)
                .toList();

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<StudentDtoOut> studentDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(studentDtoOuts).isNotNull().isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentDtoOuts.size())
                            .forEach(index -> {
                                StudentDtoOut studentDtoOut = studentDtoOuts.get(index);
                                StudentDtoOut expected = expectedResult.get(index);
                                assertThat(studentDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(studentDtoOut.getName()).isEqualTo(expected.getName());
                                assertThat(studentDtoOut.getAge()).isEqualTo(expected.getAge());
                            });

                });

        // when age is sent as a parameter
        int age = students.get(0).getAge();

        students = students.stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());

        List<StudentDtoOut> expectedResult2 = students.stream()
                .filter(student -> student.getAge() == age)
                .map(studentMapper::toDto)
                .toList();

        when(studentRepository.findAllByAge(age)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student?age={age}", age)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<StudentDtoOut> studentDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(studentDtoOuts).isNotNull().isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentDtoOuts.size())
                            .forEach(index -> {
                                StudentDtoOut studentDtoOut = studentDtoOuts.get(index);
                                StudentDtoOut expected = expectedResult2.get(index);
                                assertThat(studentDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(studentDtoOut.getName()).isEqualTo(expected.getName());
                                assertThat(studentDtoOut.getAge()).isEqualTo(expected.getAge());
                            });
                });
    }

    @Test
    void findStudentsByAgeBetweenTest() throws Exception {
        List<Student> students = Stream.iterate(1L, id -> id + 1)
                .map(this::generateStudent)
                .limit(20)
                .toList();

        int ageFrom = students.get(0).getAge();
        int ageTo = ageFrom + 5;
        students = students.stream()
                .filter(student -> student.getAge() >= ageFrom && student.getAge() <= ageTo)
                .collect(Collectors.toList());

        List<StudentDtoOut> expectedResult = students.stream()
                .filter(student -> student.getAge() >= ageFrom && student.getAge() <= ageTo)
                .map(studentMapper::toDto)
                .toList();

        when(studentRepository.findAllByAgeBetween(ageFrom, ageTo)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filter?ageFrom={ageFrom}&ageTo={ageTo}", ageFrom, ageTo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<StudentDtoOut> studentDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(studentDtoOuts).isNotNull().isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentDtoOuts.size())
                            .forEach(index -> {
                                StudentDtoOut studentDtoOut = studentDtoOuts.get(index);
                                StudentDtoOut expected = expectedResult.get(index);
                                assertThat(studentDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(studentDtoOut.getName()).isEqualTo(expected.getName());
                                assertThat(studentDtoOut.getAge()).isEqualTo(expected.getAge());
                            });
                });
    }

    @Test
    void createStudentTest() throws Exception {
        StudentDtoIn studentDtoIn = generateDto();

        Faculty faculty = generateFaculty();

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        Student student = new Student();
        student.setId(1L);
        student.setName(studentDtoIn.getName());
        student.setAge(studentDtoIn.getAge());
        student.setFaculty(faculty);

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(objectMapper.writeValueAsString(studentDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getName()).isEqualTo(studentDtoIn.getName());
                    assertThat(studentDtoOut.getAge()).isEqualTo(studentDtoIn.getAge());
                });

        verify(studentRepository, times(1)).save(any(Student.class));
    }

    private StudentDtoIn generateDto() {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setName(faker.name().fullName());
        studentDtoIn.setAge(faker.random().nextInt(7, 18));
        studentDtoIn.setFacultyId(1L);
        return studentDtoIn;
    }

    private Student generateStudent(Long id) {
        Student student = new Student();
        student.setId(id);
        student.setName(faker.name().fullName());
        student.setAge(faker.random().nextInt(7, 18));
        student.setFaculty(generateFaculty());
        return student;
    }

    private Faculty generateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Faculty");
        faculty.setColor("Color");
        return faculty;
    }

}
