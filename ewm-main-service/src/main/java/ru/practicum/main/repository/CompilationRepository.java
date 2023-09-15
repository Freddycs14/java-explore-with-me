package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation as c " +
            "WHERE c.pinned = ?1 OR (c.pinned = FALSE OR c.pinned = TRUE) ")
    List<Compilation> findAllByPinned(Boolean pinned, Pageable page);
}
