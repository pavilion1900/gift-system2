package ru.clevertec.integration.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.clevertec.entity.Tag;
import ru.clevertec.integration.IntegrationTestBase;
import ru.clevertec.repository.TagRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static ru.clevertec.testdata.TagUtil.tagForSaveWithId;
import static ru.clevertec.testdata.TagUtil.tagForSaveWithoutId;
import static ru.clevertec.testdata.TagUtil.tagForUpdateWithId;
import static ru.clevertec.testdata.TagUtil.tagWithId1;
import static ru.clevertec.testdata.TagUtil.tags;

@RequiredArgsConstructor
public class TagRepositoryTest extends IntegrationTestBase {

    private final TagRepository tagRepository;

    @Test
    void checkFindAll() {
        List<Tag> actual = tagRepository.findAll(PageRequest.of(0, 20)).getContent();
        assertEquals(tags(), actual);
    }

    @Test
    void checkFindByIdIfTagIdExist() {
        Optional<Tag> optional = tagRepository.findById(1);
        optional.ifPresent(tag -> assertEquals(tagWithId1(), tag));
    }

    @Test
    void checkFindByNameIfTagNameExist() {
        Optional<Tag> optional = tagRepository.findByNameIgnoreCase("new");
        optional.ifPresent(tag -> assertEquals(tagWithId1(), tag));
    }

    @Test
    void checkFindMostWidelyUsedTag() {
        Optional<Tag> optional = tagRepository.findMostWidelyUsedTag();
        optional.ifPresent(tag -> assertEquals(tagWithId1(), tag));
    }

    @Test
    void checkSaveIfTagHasUniqueName() {
        Tag actual = tagRepository.save(tagForSaveWithoutId());
        assertEquals(tagForSaveWithId(), actual);
    }

    @Test
    void checkUpdateIfTagHasUniqueIdAndUniqueName() {
        Tag actual = tagRepository.save(tagForUpdateWithId());
        tagRepository.flush();
        assertEquals(tagForUpdateWithId(), actual);
    }

    @Test
    void checkDeleteIfTagHasUniqueId() {
        tagRepository.deleteById(2);
        Optional<Tag> optional = tagRepository.findById(2);
        tagRepository.flush();
        assertFalse(optional.isPresent());
    }
}
