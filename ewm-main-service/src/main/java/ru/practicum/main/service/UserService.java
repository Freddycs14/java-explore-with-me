package ru.practicum.main.service;

import ru.practicum.main.dto.AdminUserDto;
import ru.practicum.main.dto.UserDto;

import java.util.List;

public interface UserService {
    AdminUserDto createUser(UserDto userDto);

    void removeUser(Long userId);

    List<AdminUserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
