package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
           "WHERE LOWER(c.text) LIKE CONCAT('%', LOWER(?1), '%') ")
    List<Comment> findAllByText(String text);

    List<Comment> findAllByAuthorId(long userId);
}
