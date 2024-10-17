package ru.hogwarts.school.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final FacultyMapper facultyMapper;
    private final StudentMapper studentMapper;
    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

    @Autowired
    public FacultyServiceImpl(final FacultyRepository facultyRepository,
                              final StudentRepository studentRepository,
                              final FacultyMapper facultyMapper,
                              final StudentMapper studentMapper
    ) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.facultyMapper = facultyMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public FacultyDtoOut create(FacultyDtoIn facultyDtoIn) {
        logger.info("Was invoked method for creating a Faculty");

        return facultyMapper.toDto(
                facultyRepository.save(
                        facultyMapper.toEntity(facultyDtoIn)
                )
        );
    }

    @Override
    public FacultyDtoOut get(Long id) {
        logger.info("Was invoked method for getting the Faculty with id = {}", id);

        return facultyRepository
                .findById(id)
                .map(facultyMapper::toDto)
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    @Override
    public FacultyDtoOut update(Long id, FacultyDtoIn facultyDtoIn) {
        logger.info("Was invoked method for updating the Faculty with id = {}", id);

        return facultyRepository
                .findById(id)
                .map(oldFaculty -> {
                    oldFaculty.setName(facultyDtoIn.getName());
                    oldFaculty.setColor(facultyDtoIn.getColor());

                    logger.warn("The Faculty with id = {} was successfully updated", id);

                    return facultyMapper.toDto(facultyRepository.save(oldFaculty));
                })
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    @Override
    public FacultyDtoOut delete(Long id) {
        logger.info("Was invoked method for deleting the Faculty with id = {}", id);

        Faculty faculty = facultyRepository
                .findById(id)
                .orElseThrow(() -> new FacultyNotFoundException(id));

        facultyRepository.delete(faculty);

        logger.warn("The Faculty with id = {} was successfully deleted", id);

        return facultyMapper.toDto(faculty);
    }

    @Override
    public List<FacultyDtoOut> findByColorOrName(String colorOrName) {
        logger.info("Was invoked method for finding the Faculty with color or name = {}", colorOrName);

        return facultyRepository
                .findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase(colorOrName, colorOrName)
                .stream()
                .map(facultyMapper::toDto)
                .toList();
    }

    @Override
    public List<FacultyDtoOut> findAll(@Nullable String color) {
        logger.info("Was invoked method for finding all the Faculties or the Faculties with color = {}", color);

        return Optional.ofNullable(color)
                .map(facultyRepository::findAllByColor)
                .orElseGet(facultyRepository::findAll)
                .stream()
                .map(facultyMapper::toDto)
                .toList();
    }

    @Override
    public List<StudentDtoOut> getFacultyStudents(Long id) {
        logger.info("Was invoked method for getting all the students of the Faculty with id = {}", id);

        return studentRepository
                .findAllByFaculty_Id(id)
                .stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public String getTheLongestFacultyName() {
        return facultyRepository.findAll()
                .stream()
                .map(Faculty::getName)
                .max(Comparator.comparing(String::length))
                .orElse("");
    }

}
