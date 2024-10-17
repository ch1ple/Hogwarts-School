package ru.hogwarts.school.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hogwarts.school.controller.AvatarController;
import ru.hogwarts.school.dto.AvatarDtoIn;
import ru.hogwarts.school.dto.AvatarDtoOut;
import ru.hogwarts.school.entity.Avatar;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Optional;

@Component
public class AvatarMapper {

    private final int port;
    private final StudentRepository studentRepository;

    @Autowired
    public AvatarMapper(@Value("${server.port}") int port, StudentRepository studentRepository) {
        this.port = port;
        this.studentRepository = studentRepository;
    }

    public AvatarDtoOut toDto(Avatar avatar) {
        AvatarDtoOut avatarDtoOut = new AvatarDtoOut();

        avatarDtoOut.setId(avatar.getId());
        avatarDtoOut.setFilePath(avatar.getFilePath());
        avatarDtoOut.setFileSize(avatar.getFileSize());
        avatarDtoOut.setMediaType(avatar.getMediaType());
        avatarDtoOut.setStudentId(avatar.getStudent().getId());

        avatarDtoOut.setAvatarUrl(UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .pathSegment(AvatarController.BASE_PATH, avatar.getId().toString(), "avatarFromDb")
                .toUriString());

        return avatarDtoOut;
    }

    public Avatar toEntity(AvatarDtoIn avatarDtoIn) {
        Avatar avatar = new Avatar();

        avatar.setFilePath(avatarDtoIn.getFilePath());
        avatar.setFileSize(avatarDtoIn.getFileSize());
        avatar.setMediaType(avatarDtoIn.getMediaType());

        Optional.of(avatarDtoIn.getStudentId())
                .ifPresent(studentId ->
                        avatar.setStudent(
                                studentRepository.findById(studentId)
                                        .orElseThrow(() -> new StudentNotFoundException(studentId))
                        )
                );

        return avatar;
    }
}
