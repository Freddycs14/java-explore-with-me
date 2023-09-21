package ru.practicum.main.service;

import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto updateCommentByUser(Long userId, Long comId, NewCommentDto commentDto);

    void removeCommentByUser(Long userId, Long comId);

    void adminRemoveComment(Long comId);

    List<CommentDto> searchUserComment(Long userId);

    List<CommentDto> search(String text);

    CommentDto getComment(Long comId);
}
