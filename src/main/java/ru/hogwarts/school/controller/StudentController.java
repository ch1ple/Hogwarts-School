package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping(path = "/student")
@Tag(name = "Контроллер по работе со студентами")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(final StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping(path = "{id}")
    public StudentDtoOut get(@PathVariable(value = "id") Long id) {
        return studentService.get(id);
    }

    @PostMapping
    public StudentDtoOut create(@RequestBody StudentDtoIn studentDtoIn) {
        return studentService.create(studentDtoIn);
    }

    @PutMapping(path = "{id}")
    public StudentDtoOut update(@PathVariable(value = "id") Long id,
                                @RequestBody StudentDtoIn studentDtoIn
    ) {
        return studentService.update(id, studentDtoIn);
    }

    @DeleteMapping(path = "{id}")
    public StudentDtoOut delete(@PathVariable(value = "id") Long id) {
        return studentService.delete(id);
    }

    @GetMapping
    public List<StudentDtoOut> findAll(@RequestParam(required = false) Integer age) {
        return studentService.findAll(age);
    }

    @GetMapping(path = "filter")
    public List<StudentDtoOut> findByAgeBetween(@RequestParam Integer ageFrom,
                                                @RequestParam Integer ageTo) {
        return studentService.findByAgeBetween(ageFrom, ageTo);
    }

    @GetMapping(path = "{id}/faculty")
    public FacultyDtoOut getFacultyForStudent(@PathVariable(value = "id") Long id) {
        return studentService.getFacultyForStudent(id);
    }

    @PatchMapping(value = "{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentDtoOut uploadAvatar(@PathVariable(value = "id") Long id,
                                      @RequestPart(name = "avatar") MultipartFile multipartFile
    ) {
        return studentService.uploadAvatar(id, multipartFile);
    }

    @GetMapping(path = "allStudentsCount")
    public int getSchoolStudentsCount() {
        return studentService.countAllStudentsInTheSchool();
    }

    @GetMapping(path = "avgStudentsAge")
    public double getAverageAgeOfStudents() {
        return studentService.getAverageAgeOfStudents();
    }

    @GetMapping(path = "lastStudents")
    public List<StudentDtoOut> getLastStudents(
            @RequestParam(value = "count", required = false, defaultValue = "5") int count
    ) {
        return studentService.getLastStudents(Math.abs(count));
    }

    @GetMapping(path = "name-starts-with-letter")
    public ResponseEntity<List<String>> filterStudentsByNameStartsWith(@RequestParam(value = "letter") String letter) {
        List<String> filteredStudents = studentService.filterStudentsByNameStartsWith(letter);
        return ResponseEntity.ok(filteredStudents);
    }

    @GetMapping(path = "average-age-of-students")
    public ResponseEntity<Double> getAverageAgeOfStudentsByStreamAPI() {
        double averageAge = studentService.getAverageAgeOfStudentsByStreamAPI();
        return ResponseEntity.ok(averageAge);
    }

    @GetMapping(path = "threads")
    public void testParallelThreads() {
        studentService.testParallelThreads();
    }

    @GetMapping(path = "sync-threads")
    public void testSynchronizedThreads() {
        studentService.testSynchronizedThreads();
    }

}
