package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.dto.UserDto;
import ru.clevertec.entity.User;

@Mapper(uses = OrderMapper.class)
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "orders", ignore = true)
    User toEntity(UserDto userDto);
}
