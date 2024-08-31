package ru.hogwarts.school.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.util.List;


@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadAvatar(@RequestPart("avatar") MultipartFile multipartFile,
                             @RequestParam long studentId) {
        avatarService.uploadAvatar(multipartFile, studentId);
    }

    @GetMapping
    public List<Avatar> getAvatarPage(@RequestParam("page") int pageNumber,
                                      @RequestParam("size") int pageSize) {
        return avatarService.getAvatarPage(pageNumber, pageSize);
    }

}

