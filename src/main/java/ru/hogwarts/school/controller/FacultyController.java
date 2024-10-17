package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping(path = "/faculty")
@Tag(name = "Контроллер по работе с факультетами")
public class FacultyController {

    private final FacultyService facultyService;

    @Autowired
    public FacultyController(final FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping(path = "{id}")
    public FacultyDtoOut get(@PathVariable(value = "id") Long id) {
        return facultyService.get(id);
    }

    @PostMapping
    public FacultyDtoOut create(@RequestBody FacultyDtoIn facultyDtoIn) {
        return facultyService.create(facultyDtoIn);
    }

    @PutMapping(path = "{id}")
    public FacultyDtoOut update(@PathVariable(value = "id") Long id,
                                @RequestBody FacultyDtoIn facultyDtoIn
    ) {
        return facultyService.update(id, facultyDtoIn);
    }

    @DeleteMapping(path = "{id}")
    public FacultyDtoOut delete(@PathVariable(value = "id") Long id) {
        return facultyService.delete(id);
    }

    @GetMapping
    public List<FacultyDtoOut> findAll(@RequestParam(required = false) String color) {
        return facultyService.findAll(color);
    }

    @GetMapping(path = "filter")
    public List<FacultyDtoOut> findByColorOrName(@RequestParam String colorOrName) {
        return facultyService.findByColorOrName(colorOrName);
    }

    @GetMapping(path = "{id}/students")
    public List<StudentDtoOut> getFacultyStudents(@PathVariable(name = "id") Long id) {
        return facultyService.getFacultyStudents(id);
    }

    @GetMapping(path = "longest-name")
    public ResponseEntity<String> getTheLongestFacultyName() {
        String facultyName = facultyService.getTheLongestFacultyName();
        return ResponseEntity.ok(facultyName);
    }

}
