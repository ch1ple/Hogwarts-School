package ru.hogwarts.school.service.controller;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.repository.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTemplateTest {

    private final Faker faker = new Faker();
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private StudentController studentController;
    @Autowired
    private StudentRepository studentRepository;

    @AfterEach
    public void clean() {
        studentRepository.deleteAll();
    }

    @Test
    void contextLoads() throws Exception {
        assertThat(studentController).isNotNull();
    }

    @Test
    void testGetStudents() throws Exception {
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/student", String.class))
//                .isNotNull();
                .isNotEmpty();
    }

    @Test
    public StudentDtoOut createStudentTest() throws FacultyNotFoundException {
        StudentDtoIn studentDtoIn = generate();

        ResponseEntity<StudentDtoOut> responseEntity = this.testRestTemplate.postForEntity(
                "http://localhost:" + port + "/student",
                studentDtoIn,
                StudentDtoOut.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentDtoOut studentDtoOut = responseEntity.getBody();

        assertThat(studentDtoOut).isNotNull();
        assertThat(studentDtoOut.getId()).isNotZero();
        assertThat(studentDtoOut.getAge()).isEqualTo(studentDtoIn.getAge());
        assertThat(studentDtoOut.getName()).isEqualTo(studentDtoIn.getName());

        return studentDtoOut;
    }

    @Test
    void updateStudentTest() throws FacultyNotFoundException {
        // сначала создаем Студента
        StudentDtoOut created = createStudentTest();

        // затем меняем Студента
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setName(faker.name().fullName());
        studentDtoIn.setAge(created.getAge());

        ResponseEntity<StudentDtoOut> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/student/" + created.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(studentDtoIn),
                StudentDtoOut.class
        );

        System.out.println(responseEntity.getStatusCode());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentDtoOut studentDtoOut = responseEntity.getBody();

        assertThat(studentDtoOut).isNotNull();
        assertThat(studentDtoOut.getId()).isEqualTo(created.getId());
        assertThat(studentDtoOut.getAge()).isEqualTo(studentDtoIn.getAge());
        assertThat(studentDtoOut.getName()).isEqualTo(studentDtoIn.getName());

        // negative test - Student not found
        long notFoundStudentId = created.getId() + 1;
        ResponseEntity<String> stringResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/student/" + notFoundStudentId,
                HttpMethod.PUT,
                new HttpEntity<>(studentDtoIn),
                String.class
        );

        System.out.println(stringResponseEntity.getStatusCode());

        assertThat(stringResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(stringResponseEntity.getBody())
                .isEqualTo("Студент с id = " + notFoundStudentId + " не найден");
    }

    public StudentDtoIn generate() {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setAge(faker.random().nextInt(7, 18));
        studentDtoIn.setName(faker.name().fullName());
        return studentDtoIn;
    }

}
