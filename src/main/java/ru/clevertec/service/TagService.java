package ru.clevertec.service;

import org.springframework.data.domain.Pageable;
import ru.clevertec.dto.TagDto;

import java.util.List;

public interface TagService {

    List<TagDto> findAll(Pageable pageable);

    TagDto findById(Integer id);

    TagDto findByNameIgnoreCase(String tagName);

    TagDto findMostWidelyUsedTag();

    Integer findLastSequenceValue();

    TagDto save(TagDto tagDto);

    TagDto update(Integer id, TagDto tagDto);

    void delete(Integer id);

    TagDto saveOrUpdate(TagDto tagDto);
}
