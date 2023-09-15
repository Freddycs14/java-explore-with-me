package ru.practicum.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilations(NewCompilation newCompilation, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(newCompilation.getPinned())
                .title(newCompilation.getTitle())
                .build();
    }

    public CompilationDto toCompilationDto(CompilationShort compilations) {
        return CompilationDto.builder()
                .id(compilations.getId())
                .events(EventMapper.toListEventShortDto(compilations.getEvents()))
                .pinned(compilations.getPinned())
                .title(compilations.getTitle())
                .build();
    }

    public NewCompilation toNewCompilation(NewCompilationDto newCompilationDto) {
        return NewCompilation.builder()
                .events(new ArrayList<>(newCompilationDto.getEvents()))
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public CompilationShort toCompilationShort(Compilation compilation, List<EventShort> list) {
        return CompilationShort.builder()
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(list)
                .id(compilation.getId())
                .build();
    }

    public CompilationDto toCompilationDtoFromCompilation(Compilation compilation) {
        return CompilationDto.builder()
                .events(EventMapper.toListEventShortDto(EventMapper.toListEventShort(new ArrayList<>(compilation.getEvents()))))
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public List<CompilationDto> toListCompilationDtoFromCompilation(List<Compilation> list) {
        return list.stream().map(CompilationMapper::toCompilationDtoFromCompilation).collect(Collectors.toList());
    }


}
