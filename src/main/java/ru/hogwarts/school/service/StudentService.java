package ru.hogwarts.school.service;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;

import java.util.List;

public interface StudentService {

    StudentDtoOut create(StudentDtoIn studentDtoIn);

    StudentDtoOut get(Long id);

    StudentDtoOut update(Long id, StudentDtoIn studentDtoIn);

    StudentDtoOut delete(Long id);

    List<StudentDtoOut> findAll(@Nullable Integer age);

    List<StudentDtoOut> findByAgeBetween(Integer ageFrom, Integer ageTo);

    FacultyDtoOut getFacultyForStudent(Long id);

    StudentDtoOut uploadAvatar(Long id, MultipartFile file);

    int countAllStudentsInTheSchool();

    double getAverageAgeOfStudents();

    List<StudentDtoOut> getLastStudents(int count);

    List<String> filterStudentsByNameStartsWith(String letter);

    double getAverageAgeOfStudentsByStreamAPI();

    void testParallelThreads();

    void testSynchronizedThreads();

}
