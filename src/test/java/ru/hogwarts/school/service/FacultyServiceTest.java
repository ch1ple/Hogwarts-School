package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.impl.FacultyServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles(value = "test")
@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository repositoryMock;

    @Mock
    private FacultyMapper facultyMapper;

    @InjectMocks
    private FacultyServiceImpl service;

    @Test
    void shouldAddFacultyTest() {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName("Name");
        faculty1.setColor("Color");
        when(repositoryMock.save(faculty1)).thenReturn(faculty1);

        Faculty faculty2 = new Faculty();
        faculty2.setId(1L);
        faculty2.setName("Name");
        faculty2.setColor("Color");

        FacultyDtoIn facultyDtoIn = new FacultyDtoIn("Name", "Color");
        FacultyDtoOut facultyDtoOut = new FacultyDtoOut(1L, "Name", "Color");

        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDtoOut);
        when(facultyMapper.toEntity(facultyDtoIn)).thenReturn(faculty2);

        assertThat(facultyDtoOut).isSameAs(service.create(facultyDtoIn));

        verify(repositoryMock).save(faculty1);
        verify(facultyMapper).toDto(faculty1);
        verify(facultyMapper).toEntity(facultyDtoIn);
        verify(repositoryMock, times(1)).save(faculty1);
        verify(repositoryMock, times(1)).save(faculty2);
    }

    @Test
    void shouldAddFacultyNegativeTest() {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName("Name");
        faculty1.setColor("Color");

        FacultyDtoIn facultyDtoIn = new FacultyDtoIn("Name", "Color");

        when(facultyMapper.toEntity(facultyDtoIn))
                .thenThrow(new FacultyNotFoundException(1L));
        assertThrows(FacultyNotFoundException.class,
                () -> service.create(facultyDtoIn));

        verify(facultyMapper).toEntity(facultyDtoIn);
        verify(repositoryMock, never()).save(faculty1);
    }

}