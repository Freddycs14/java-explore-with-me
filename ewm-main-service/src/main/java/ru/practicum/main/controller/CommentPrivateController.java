package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.service.CommentService;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {
    private final CommentService service;

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @RequestParam Long eventId,
                                    @RequestBody @Validated NewCommentDto commentDto) {
        log.info("Создание комментария");
        return service.createComment(userId, eventId, commentDto);
    }

    @PatchMapping("/comment/{comId}")
    public CommentDto updateCommentByUser(@PathVariable Long userId, @PathVariable Long comId,
                                          @RequestBody @Validated NewCommentDto commentDto) {
        log.info("Изменение комментария");
        return service.updateCommentByUser(userId, comId, commentDto);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCommentByUser(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Удаление комментария пользователем");
        service.removeCommentByUser(userId, commentId);
    }
}
