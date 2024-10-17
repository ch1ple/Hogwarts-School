package ru.hogwarts.school.service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FacultyControllerTest {

    private final Faker faker = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FacultyMapper facultyMapper;
    @MockBean
    private FacultyRepository facultyRepository;
    @MockBean
    private StudentRepository studentRepository;

    @Test
    void createFacultyTest() throws Exception {
        FacultyDtoIn facultyDtoIn = generateDto();

        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(facultyDtoIn.getName());
        faculty.setColor(facultyDtoIn.getColor());

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(objectMapper.writeValueAsString(facultyDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getName()).isEqualTo(facultyDtoIn.getName());
                    assertThat(facultyDtoOut.getColor()).isEqualTo(facultyDtoIn.getColor());
                });
        verify(facultyRepository, times(1)).save(any(Faculty.class));
    }

    @Test
    void updateFacultyTest() throws Exception {
        FacultyDtoIn facultyDtoIn = generateDto();

        Faculty oldFaculty = generate(1L);

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(oldFaculty));

        oldFaculty.setName(facultyDtoIn.getName());
        oldFaculty.setColor(facultyDtoIn.getColor());

        when(facultyRepository.save(any(Faculty.class))).thenReturn(oldFaculty);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/1")
                        .content(objectMapper.writeValueAsString(facultyDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getName()).isEqualTo(facultyDtoIn.getName());
                    assertThat(facultyDtoOut.getColor()).isEqualTo(facultyDtoIn.getColor());
                });
        verify(facultyRepository, times(1)).save(any(Faculty.class));

        Mockito.reset(facultyRepository);

        when(facultyRepository.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/2")
                        .content(objectMapper.writeValueAsString(facultyDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).contains("id = 2");
                });
        verify(facultyRepository, never()).save(any(Faculty.class));
    }

    @Test
    void getFacultyTest() throws Exception {
        Faculty faculty = generate(1L);

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getName()).isEqualTo(faculty.getName());
                    assertThat(facultyDtoOut.getColor()).isEqualTo(faculty.getColor());
                });

        when(facultyRepository.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/2")
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
    void deleteFacultyTest() throws Exception {
        Faculty faculty = generate(1L);

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getName()).isEqualTo(faculty.getName());
                    assertThat(facultyDtoOut.getColor()).isEqualTo(faculty.getColor());
                });
        verify(facultyRepository, times(1)).delete(any(Faculty.class));

        Mockito.reset(facultyRepository);

        when(facultyRepository.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).contains("id = 2");
                });
        verify(facultyRepository, never()).delete(any(Faculty.class));
    }

    @Test
    void findAllFacultiesTest() throws Exception {
        List<Faculty> faculties = Stream.iterate(1L, id -> id + 1)
                .map(this::generate)
                .limit(20)
                .toList();
        List<FacultyDtoOut> expectedResult = faculties.stream()
                .map(facultyMapper::toDto)
                .toList();

        when(facultyRepository.findAll()).thenReturn(faculties);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<FacultyDtoOut> facultyDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(facultyDtoOuts).isNotNull().isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(facultyDtoOuts.size())
                            .forEach(index -> {
                                FacultyDtoOut facultyDtoOut = facultyDtoOuts.get(index);
                                FacultyDtoOut expected = expectedResult.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                                assertThat(facultyDtoOut.getColor()).isEqualTo(expected.getColor());
                            });
                });

        // color
        String color = faculties.get(0).getColor();
        faculties = faculties.stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .collect(Collectors.toList());
        List<FacultyDtoOut> expectedResult2 = faculties.stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .map(facultyMapper::toDto)
                .toList();
        when(facultyRepository.findAllByColor(color)).thenReturn(faculties);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?color={color}", color)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<FacultyDtoOut> facultyDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(facultyDtoOuts).isNotNull().isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(facultyDtoOuts.size())
                            .forEach(index -> {
                                FacultyDtoOut facultyDtoOut = facultyDtoOuts.get(index);
                                FacultyDtoOut expected = expectedResult2.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                                assertThat(facultyDtoOut.getColor()).isEqualTo(expected.getColor());
                            });
                });
    }

    @Test
    void findFacultiesByColorOrNameTest() throws Exception {
        List<Faculty> faculties = Stream.iterate(1L, id -> id + 1)
                .map(this::generate)
                .limit(20)
                .toList();

        String color = faculties.get(0).getColor();
        String name = faculties.get(0).getName();
        faculties = faculties.stream()
                .filter(faculty -> faculty.getColor().equals(color) || faculty.getName().equals(name))
                .collect(Collectors.toList());

        List<FacultyDtoOut> expectedResult = faculties.stream()
                .filter(faculty -> faculty.getColor().equals(color) || faculty.getName().equals(name))
                .map(facultyMapper::toDto)
                .toList();

        when(facultyRepository.findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase(name, name))
                .thenReturn(faculties);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter?colorOrName={name}", name)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<FacultyDtoOut> facultyDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(facultyDtoOuts).isNotNull().isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(facultyDtoOuts.size())
                            .forEach(index -> {
                                FacultyDtoOut facultyDtoOut = facultyDtoOuts.get(index);
                                FacultyDtoOut expected = expectedResult.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                                assertThat(facultyDtoOut.getColor()).isEqualTo(expected.getColor());
                            });
                });
    }

    private FacultyDtoIn generateDto() {
        FacultyDtoIn facultyDtoIn = new FacultyDtoIn();
        facultyDtoIn.setName(faker.harryPotter().house());
        facultyDtoIn.setColor(faker.color().name());
        return facultyDtoIn;
    }

    private Faculty generate(Long id) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return faculty;
    }

}
