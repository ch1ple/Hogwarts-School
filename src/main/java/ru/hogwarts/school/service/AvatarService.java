package ru.hogwarts.school.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDtoOut;
import ru.hogwarts.school.entity.Avatar;
import ru.hogwarts.school.entity.Student;

import java.util.List;

public interface AvatarService {

    Avatar findAvatar(Long id);

    Avatar create(Student student, MultipartFile file);

    Pair<byte[], String> getFromDB(Long id);

    Pair<byte[], String> getFromFS(Long id);

    List<AvatarDtoOut> getAllAvatars(Integer pageNumber, Integer pageSize);

}
