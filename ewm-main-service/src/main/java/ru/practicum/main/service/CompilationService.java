package ru.practicum.main.service;

import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void removeCompilation(Long compilationId);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationDto);

    List<CompilationDto> getCompilationsList(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compilationId);
}
