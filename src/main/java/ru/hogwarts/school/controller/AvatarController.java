package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.AvatarDtoOut;
import ru.hogwarts.school.service.AvatarService;

import java.util.List;

@RestController
@RequestMapping(path = AvatarController.BASE_PATH)
@Tag(name = "Контроллер по работе с аватарами")
public class AvatarController {

    public static final String BASE_PATH = "avatar";

    private final AvatarService avatarService;

    @Autowired
    public AvatarController(final AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping
    public List<AvatarDtoOut> getAllAvatars(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                            @RequestParam(value = "size", required = false, defaultValue = "3") Integer pageSize
    ) {
        return avatarService.getAllAvatars(Math.abs(pageNumber), Math.abs(pageSize));
    }

    @GetMapping(value = "{id}/avatarFromDb")
    public ResponseEntity<byte[]> getAvatarFromDB(@PathVariable(value = "id") Long id) {
        return build(avatarService.getFromDB(id));
    }

    @GetMapping(value = "{id}/avatarFromFile")
    public ResponseEntity<byte[]> getAvatarFromFS(@PathVariable(value = "id") Long id) {
        return build(avatarService.getFromFS(id));
    }

    private ResponseEntity<byte[]> build(Pair<byte[], String> pair) {
        byte[] data = pair.getFirst();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .contentLength(data.length)
                .body(data);
    }

}
