package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.dto.TagDto;
import ru.clevertec.entity.Tag;

@Mapper
public interface TagMapper {

    TagDto toDto(Tag tag);

    Tag toEntity(TagDto tagDto);
}
