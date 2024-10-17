package ru.hogwarts.school.service.controller;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.repository.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerContainerTest {

    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            "postgres:14.4")
            .withUsername("student")
            .withPassword("chocolatefrog")
            .withDatabaseName("hogwarts");

    static {
        POSTGRE_SQL_CONTAINER.start();
        System.setProperty(
                "spring.datasource.url",
                POSTGRE_SQL_CONTAINER.getJdbcUrl()
        );
    }

    private final Faker faker = new Faker();
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private StudentRepository studentRepository;

    @AfterAll
    public static void afterAll() {
        POSTGRE_SQL_CONTAINER.stop();
    }

    @AfterEach
    public void clean() {
        studentRepository.deleteAll();
    }

    @Test
    public StudentDtoOut createStudent() {
        StudentDtoIn studentDtoIn = generate();

        ResponseEntity<StudentDtoOut> responseEntity = testRestTemplate.postForEntity(
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

    public StudentDtoIn generate() {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setAge(faker.random().nextInt(7, 18));
        studentDtoIn.setName(faker.name().fullName());
        return studentDtoIn;
    }

}
