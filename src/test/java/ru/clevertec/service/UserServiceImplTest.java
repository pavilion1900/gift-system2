package ru.clevertec.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.clevertec.dto.UserDto;
import ru.clevertec.entity.User;
import ru.clevertec.exception.EntityNotFoundException;
import ru.clevertec.mapper.UserMapper;
import ru.clevertec.repository.UserRepository;
import ru.clevertec.service.impl.UserServiceImpl;
import ru.clevertec.testdata.UserUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void checkFindAll() {
        doReturn(new PageImpl<>(UserUtil.users()))
                .when(userRepository).findAll(UserUtil.pageable());
        Mockito.doReturn(UserUtil.userDtoWithId1())
                .when(userMapper).toDto(UserUtil.userWithId1());
        Mockito.doReturn(UserUtil.userDtoWithId2())
                .when(userMapper).toDto(UserUtil.userWithId2());
        Mockito.doReturn(UserUtil.userDtoWithId3())
                .when(userMapper).toDto(UserUtil.userWithId3());
        List<UserDto> actual = userService.findAll(UserUtil.pageable());
        Assertions.assertEquals(UserUtil.dtoUsers(), actual);
        verify(userRepository).findAll(UserUtil.pageable());
        verify(userMapper, times(3)).toDto(any(User.class));
    }

    @Test
    void checkFindByIdIfUserIdExist() {
        doReturn(Optional.of(UserUtil.userWithId1()))
                .when(userRepository).findById(1);
        Mockito.doReturn(UserUtil.userDtoWithId1())
                .when(userMapper).toDto(UserUtil.userWithId1());
        UserDto actual = userService.findById(1);
        Assertions.assertEquals(UserUtil.userDtoWithId1(), actual);
        verify(userRepository).findById(1);
        verify(userMapper).toDto(UserUtil.userWithId1());
    }

    @Test
    void throwExceptionIfUserIdNotExist() {
        doReturn(Optional.empty())
                .when(userRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> userService.findById(1));
    }

    @Test
    void checkFindByNameIfUserNameExist() {
        doReturn(Optional.of(UserUtil.userWithId1()))
                .when(userRepository).findByNameIgnoreCase("Ivanov");
        Mockito.doReturn(UserUtil.userDtoWithId1())
                .when(userMapper).toDto(UserUtil.userWithId1());
        UserDto actual = userService.findByNameIgnoreCase("Ivanov");
        Assertions.assertEquals(UserUtil.userDtoWithId1(), actual);
        verify(userRepository).findByNameIgnoreCase("Ivanov");
        verify(userMapper).toDto(UserUtil.userWithId1());
    }

    @Test
    void throwExceptionIfUserNameNotExist() {
        doReturn(Optional.empty())
                .when(userRepository).findByNameIgnoreCase("Ivanov");
        assertThrows(EntityNotFoundException.class, () -> userService.findByNameIgnoreCase("Ivanov"));
    }
}
