package ru.clevertec.integration.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.clevertec.entity.User;
import ru.clevertec.integration.IntegrationTestBase;
import ru.clevertec.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.clevertec.testdata.UserUtil.userWithId1;
import static ru.clevertec.testdata.UserUtil.users;

@RequiredArgsConstructor
public class UserRepositoryTest extends IntegrationTestBase {

    private final UserRepository userRepository;

    @Test
    void checkFindAll() {
        List<User> actual = userRepository.findAll(PageRequest.of(0, 20)).getContent();
        assertEquals(users(), actual);
    }

    @Test
    void checkFindByIdIfTagIdExist() {
        Optional<User> optional = userRepository.findById(1);
        optional.ifPresent(user -> assertEquals(userWithId1(), user));
    }

    @Test
    void checkFindByNameIfTagNameExist() {
        Optional<User> optional = userRepository.findByNameIgnoreCase("Ivanov");
        optional.ifPresent(user -> assertEquals(userWithId1(), user));
    }
}
