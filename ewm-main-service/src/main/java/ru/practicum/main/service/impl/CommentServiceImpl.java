package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.mappers.CommentMapper;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.User;
import ru.practicum.main.model.enums.EventState;
import ru.practicum.main.repository.CommentRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.CommentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto) {
        if (commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя комментировать еще не опубликованное событие");
        }
        Comment comment = Comment.builder()
                .author(author)
                .event(event)
                .createTime(LocalDateTime.now())
                .text(commentDto.getText())
                .build();
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(Long userId, Long comId, NewCommentDto commentDto) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Только автор может изменить комментарий");
        }
        comment.setText(commentDto.getText());
        comment.setPatchTime(LocalDateTime.now().withNano(0));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void removeCommentByUser(Long userId, Long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Только автор может удалить комментарий");
        }
        commentRepository.deleteById(comId);
    }

    @Override
    @Transactional
    public void adminRemoveComment(Long comId) {
        if (!commentRepository.existsById(comId)) {
            throw new NotFoundException("Комментарий не найден");
        }
        commentRepository.deleteById(comId);
    }

    @Override
    @Transactional
    public List<CommentDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Comment> commentSearch = commentRepository.findAllByText(text);
        return commentSearch.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentDto> searchUserComment(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Comment> list = commentRepository.findAllByAuthorId(userId);
        return list.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto getComment(Long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        return CommentMapper.toCommentDto(comment);
    }
}
