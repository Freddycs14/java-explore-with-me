package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.service.CommentService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class CommentAdminController {
    private final CommentService service;

    @GetMapping("comment/search")
    public List<CommentDto> search(@RequestParam String text) {
        log.info("Поиск комментария по тексту");
        return service.search(text);
    }

    @GetMapping("/users/{userId}/comment")
    public List<CommentDto> searchUserComment(@PathVariable Long userId) {
        log.info("Поиск комментариев пользователя");
        return service.searchUserComment(userId);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveComment(@PathVariable Long commentId) {
        log.info("Удаление комментария администратором");
        service.adminRemoveComment(commentId);
    }
}
