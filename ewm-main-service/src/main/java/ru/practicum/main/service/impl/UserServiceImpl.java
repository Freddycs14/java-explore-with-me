package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.AdminUserDto;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mappers.UserMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public AdminUserDto createUser(UserDto userDto) {
        checkUser(userDto.getName());
        User user = UserMapper.toUser(userDto);
        User createUser = userRepository.save(user);
        return UserMapper.toAdminUserDto(createUser);
    }

    @Override
    public void removeUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.deleteById(userId);
    }

    @Override
    public List<AdminUserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            users = userRepository.findAllById(ids);
        }
        return users.stream()
                .map(UserMapper::toAdminUserDto)
                .collect(Collectors.toList());
    }

    private void checkUser(String name) {
        if (userRepository.findUserByName(name) != null) {
            throw new ConflictException("Неверное имя пользователя");
        }
    }
}
