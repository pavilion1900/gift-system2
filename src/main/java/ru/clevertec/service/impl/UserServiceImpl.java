package ru.clevertec.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.dto.UserDto;
import ru.clevertec.entity.User;
import ru.clevertec.exception.EntityNotFoundException;
import ru.clevertec.mapper.UserMapper;
import ru.clevertec.repository.UserRepository;
import ru.clevertec.service.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).stream()
                .map(userMapper::toDto)
                .collect(toList());
    }

    @Override
    public UserDto findById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d not found", id)));
    }

    @Override
    public UserDto findByNameIgnoreCase(String userName) {
        return userRepository.findByNameIgnoreCase(userName)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with name %s not found", userName)));
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
