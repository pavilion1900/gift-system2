package ru.clevertec.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import ru.clevertec.dto.TagDto;
import ru.clevertec.entity.Tag;
import ru.clevertec.exception.EntityNotFoundException;
import ru.clevertec.mapper.TagMapper;
import ru.clevertec.repository.TagRepository;
import ru.clevertec.service.impl.TagServiceImpl;
import ru.clevertec.testdata.TagUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void checkFindAll() {
        doReturn(new PageImpl<>(TagUtil.tags()))
                .when(tagRepository).findAll(TagUtil.pageable());
        doReturn(TagUtil.tagDtoWithId1())
                .when(tagMapper).toDto(TagUtil.tagWithId1());
        doReturn(TagUtil.tagDtoWithId2())
                .when(tagMapper).toDto(TagUtil.tagWithId2());
        doReturn(TagUtil.tagDtoWithId3())
                .when(tagMapper).toDto(TagUtil.tagWithId3());
        doReturn(TagUtil.tagDtoWithId4())
                .when(tagMapper).toDto(TagUtil.tagWithId4());
        doReturn(TagUtil.tagDtoWithId5())
                .when(tagMapper).toDto(TagUtil.tagWithId5());
        doReturn(TagUtil.tagDtoWithId6())
                .when(tagMapper).toDto(TagUtil.tagWithId6());
        List<TagDto> actual = tagService.findAll(TagUtil.pageable());
        List<TagDto> expected = new ArrayList<>(TagUtil.dtoTags());
        assertEquals(expected, actual);
        verify(tagRepository).findAll(TagUtil.pageable());
        verify(tagMapper, times(6)).toDto(any(Tag.class));
    }

    @Test
    void checkFindByIdIfTagIdExist() {
        doReturn(Optional.of(TagUtil.tagWithId1()))
                .when(tagRepository).findById(1);
        doReturn(TagUtil.tagDtoWithId1())
                .when(tagMapper).toDto(TagUtil.tagWithId1());
        TagDto actual = tagService.findById(1);
        TagDto expected = TagUtil.tagDtoWithId1();
        assertEquals(expected, actual);
        verify(tagRepository).findById(1);
        verify(tagMapper).toDto(TagUtil.tagWithId1());
    }

    @Test
    void throwExceptionIfTagIdNotExist() {
        doReturn(Optional.empty())
                .when(tagRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> tagService.findById(1));
    }

    @Test
    void checkFindByNameIfTagNameExist() {
        doReturn(Optional.of(TagUtil.tagWithId1()))
                .when(tagRepository).findByNameIgnoreCase("short");
        doReturn(TagUtil.tagDtoWithId1())
                .when(tagMapper).toDto(TagUtil.tagWithId1());
        TagDto actual = tagService.findByNameIgnoreCase("short");
        TagDto expected = TagUtil.tagDtoWithId1();
        assertEquals(expected, actual);
        verify(tagRepository).findByNameIgnoreCase("short");
        verify(tagMapper).toDto(TagUtil.tagWithId1());
    }

    @Test
    void throwExceptionIfTagNameNotExist() {
        doReturn(Optional.empty())
                .when(tagRepository).findByNameIgnoreCase("short");
        assertThrows(EntityNotFoundException.class, () -> tagService.findByNameIgnoreCase("short"));
    }

    @Test
    void checkFindMostWidelyUsedTag() {
        doReturn(Optional.of(TagUtil.tagWithId1()))
                .when(tagRepository).findMostWidelyUsedTag();
        doReturn(TagUtil.tagDtoWithId1())
                .when(tagMapper).toDto(TagUtil.tagWithId1());
        TagDto actual = tagService.findMostWidelyUsedTag();
        TagDto expected = TagUtil.tagDtoWithId1();
        assertEquals(expected, actual);
        verify(tagRepository).findMostWidelyUsedTag();
        verify(tagMapper).toDto(TagUtil.tagWithId1());
    }

    @Test
    void checkSaveIfTagHasUniqueName() {
        doReturn(TagUtil.tagWithoutId())
                .when(tagMapper).toEntity(TagUtil.tagDtoWithoutId());
        doReturn(TagUtil.tagWithId1())
                .when(tagRepository).save(TagUtil.tagWithoutId());
        doReturn(TagUtil.tagDtoWithId1())
                .when(tagMapper).toDto(TagUtil.tagWithId1());
        TagDto actual = tagService.save(TagUtil.tagDtoWithoutId());
        TagDto expected = TagUtil.tagDtoWithId1();
        assertEquals(expected, actual);
        verify(tagMapper).toEntity(TagUtil.tagDtoWithoutId());
        verify(tagRepository).save(TagUtil.tagWithoutId());
        verify(tagMapper).toDto(TagUtil.tagWithId1());
    }

    @Test
    void throwExceptionBySaveIfTagHasNotUniqueName() {
        doReturn(TagUtil.tagWithoutId())
                .when(tagMapper).toEntity(TagUtil.tagDtoWithoutId());
        doThrow(DataIntegrityViolationException.class)
                .when(tagRepository).save(TagUtil.tagWithoutId());
        assertThrows(DataIntegrityViolationException.class, () -> tagService.save(TagUtil.tagDtoWithoutId()));
    }

    @Test
    void checkUpdateIfTagHasUniqueIdAndUniqueName() {
        doReturn(Optional.of(TagUtil.tagWithId1()))
                .when(tagRepository).findById(1);
        doReturn(TagUtil.tagForUpdateWithId())
                .when(tagRepository).save(TagUtil.tagForUpdateWithId());
        doReturn(TagUtil.tagDtoForUpdateWithId())
                .when(tagMapper).toDto(TagUtil.tagForUpdateWithId());
        TagDto actual = tagService.update(1, TagUtil.tagDtoForUpdateWithoutId());
        TagDto expected = TagUtil.tagDtoForUpdateWithId();
        assertEquals(expected, actual);
        verify(tagRepository).findById(1);
        verify(tagRepository).save(TagUtil.tagForUpdateWithId());
        verify(tagMapper).toDto(TagUtil.tagForUpdateWithId());
    }

    @Test
    void throwExceptionByUpdateIfTagIdNotExist() {
        doReturn(Optional.empty())
                .when(tagRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> tagService.update(1, TagUtil.tagDtoWithoutId()));
        verify(tagRepository, never()).save(TagUtil.tagWithId1());
    }

    @Test
    void throwExceptionByUpdateIfTagHasUniqueIdAndNotUniqueName() {
        doReturn(Optional.of(TagUtil.tagWithId2()))
                .when(tagRepository).findById(2);
        when(tagRepository.save(TagUtil.tagForUpdateWithId2()))
                .thenThrow(DataIntegrityViolationException.class);
        assertThrows(DataIntegrityViolationException.class, () -> tagService.update(2, TagUtil.tagDtoWithoutId()));
    }

    @Test
    void checkDeleteIfTagHasUniqueId() {
        doReturn(Optional.of(TagUtil.tagWithId1()))
                .when(tagRepository).findById(1);
        assertDoesNotThrow(() -> tagService.delete(1));
        verify(tagRepository).deleteById(1);
    }

    @Test
    void throwExceptionByDeleteIfTagIdNotExist() {
        doReturn(Optional.empty())
                .when(tagRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> tagService.delete(1));
        verify(tagRepository, never()).deleteById(1);
    }
}
