package ru.clevertec.service;

import org.springframework.data.domain.Pageable;
import ru.clevertec.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll(Pageable pageable);

    UserDto findById(Integer id);

    UserDto findByNameIgnoreCase(String username);

    UserDto save(UserDto userDto);
}
