package ru.hogwarts.school.service;

import org.springframework.lang.Nullable;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoOut;

import java.util.List;


public interface FacultyService {

    FacultyDtoOut create(FacultyDtoIn facultyDtoIn);

    FacultyDtoOut update(Long id, FacultyDtoIn facultyDtoIn);

    FacultyDtoOut delete(Long id);

    FacultyDtoOut get(Long id);

    List<FacultyDtoOut> findAll(@Nullable String color);

    List<FacultyDtoOut> findByColorOrName(String colorOrName);

    List<StudentDtoOut> getFacultyStudents(Long id);

    String getTheLongestFacultyName();

}
