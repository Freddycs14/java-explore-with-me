package ru.practicum.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.model.Comment;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class CommentMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Comment toComment(NewCommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .author(UserMapper.toUserDto(comment.getAuthor()))
                .event(EventMapper.toEventCommentDto(comment.getEvent()))
                .createTime(comment.getCreateTime().format(FORMATTER))
                .text(comment.getText())
                .build();
        if (comment.getPatchTime() != null) {
            commentDto.setPatchTime(comment.getPatchTime().format(FORMATTER));
        }
        return commentDto;
    }
}
