package ru.clevertec.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.clevertec.dto.CertificateDto;
import ru.clevertec.dto.TagDto;
import ru.clevertec.entity.Certificate;
import ru.clevertec.exception.EntityNotFoundException;
import ru.clevertec.mapper.CertificateMapper;
import ru.clevertec.repository.CertificateRepository;
import ru.clevertec.service.impl.CertificateServiceImpl;
import ru.clevertec.testdata.CertificateUtil;
import ru.clevertec.testdata.TagUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private TagService tagService;

    @Mock
    private CertificateMapper certificateMapper;

    @InjectMocks
    private CertificateServiceImpl certificateService;

    @Test
    void checkFindAll() {
        doReturn(new PageImpl<>(singletonList(CertificateUtil.certificateWithId1())))
                .when(certificateRepository).findAll(CertificateUtil.pageWithSizeOne());
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        List<CertificateDto> actual = certificateService.findAll(CertificateUtil.pageWithSizeOne());
        List<CertificateDto> expected = singletonList(CertificateUtil.certificateDtoWithId1());
        assertEquals(expected, actual);
        verify(certificateRepository).findAll(CertificateUtil.pageWithSizeOne());
        verify(certificateMapper).toDto(CertificateUtil.certificateWithId1());
    }

    @Test
    void checkFindAllByPartOfName() {
        doReturn(new PageImpl<>(singletonList(CertificateUtil.certificateWithId1())))
                .when(certificateRepository).findAll(any(Example.class), any(PageRequest.class));
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        List<CertificateDto> actual = certificateService.findAllByIgnoreCase("fir", null, CertificateUtil.pageable());
        List<CertificateDto> expected = singletonList(CertificateUtil.certificateDtoWithId1());
        assertEquals(expected, actual);
        verify(certificateRepository).findAll(any(Example.class), any(PageRequest.class));
        verify(certificateMapper).toDto(CertificateUtil.certificateWithId1());
    }

    @Test
    void checkFindAllByPartOfDescription() {
        doReturn(new PageImpl<>(singletonList(CertificateUtil.certificateWithId1())))
                .when(certificateRepository).findAll(any(Example.class), any(PageRequest.class));
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        List<CertificateDto> actual = certificateService.findAllByIgnoreCase(null, "fir", CertificateUtil.pageable());
        List<CertificateDto> expected = singletonList(CertificateUtil.certificateDtoWithId1());
        assertEquals(expected, actual);
        verify(certificateRepository).findAll(any(Example.class), any(PageRequest.class));
        verify(certificateMapper).toDto(CertificateUtil.certificateWithId1());
    }

    @Test
    void checkFindAllByTagName() {
        doReturn(singletonList(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findAllByTagName("new", CertificateUtil.pageWithSizeOne());
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        List<CertificateDto> actual = certificateService.findAllByTagName("new", CertificateUtil.pageWithSizeOne());
        List<CertificateDto> expected = singletonList(CertificateUtil.certificateDtoWithId1());
        assertEquals(expected, actual);
        verify(certificateRepository).findAllByTagName("new", CertificateUtil.pageWithSizeOne());
        verify(certificateMapper).toDto(CertificateUtil.certificateWithId1());
    }

    @Test
    void checkFindAllBySeveralTagNames() {
        List<String> tagNames = Arrays.asList("cheap", "short");
        List<Certificate> certificates =
                Arrays.asList(CertificateUtil.certificateWithId1(), CertificateUtil.certificateWithId3(), CertificateUtil.certificateWithId5());
        doReturn(certificates)
                .when(certificateRepository).findAllBySeveralTagNames(tagNames, CertificateUtil.pageable());
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        doReturn(CertificateUtil.certificateDtoWithId3())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId3());
        doReturn(CertificateUtil.certificateDtoWithId5())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId5());
        List<CertificateDto> actual = certificateService.findAllBySeveralTagNames(tagNames, CertificateUtil.pageable());
        List<CertificateDto> expected = Arrays.asList(
                CertificateUtil.certificateDtoWithId1(), CertificateUtil.certificateDtoWithId3(), CertificateUtil.certificateDtoWithId5());
        assertEquals(expected, actual);
        verify(certificateRepository).findAllBySeveralTagNames(tagNames, CertificateUtil.pageable());
        verify(certificateMapper, times(3)).toDto(any(Certificate.class));
    }

    @Test
    void checkFindByIdIfCertificateIdExist() {
        doReturn(Optional.of(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findById(1);
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        CertificateDto actual = certificateService.findById(1);
        CertificateDto expected = CertificateUtil.certificateDtoWithId1();
        assertEquals(expected, actual);
        verify(certificateRepository).findById(1);
        verify(certificateMapper).toDto(CertificateUtil.certificateWithId1());
    }

    @Test
    void throwExceptionIfCertificateIdNotExist() {
        doReturn(Optional.empty())
                .when(certificateRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> certificateService.findById(1));
    }

    @Test
    void checkFindByNameIfCertificateNameExist() {
        doReturn(Optional.of(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findByNameIgnoreCase("first");
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        CertificateDto actual = certificateService.findByNameIgnoreCase("first");
        CertificateDto expected = CertificateUtil.certificateDtoWithId1();
        assertEquals(expected, actual);
        verify(certificateRepository).findByNameIgnoreCase("first");
        verify(certificateMapper).toDto(CertificateUtil.certificateWithId1());
    }

    @Test
    void checkFindByNameIfCertificateNameExist2() {
        doReturn(Optional.of(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findByNameIgnoreCase("FiRsT");
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        CertificateDto actual = certificateService.findByNameIgnoreCase("FiRsT");
        CertificateDto expected = CertificateUtil.certificateDtoWithId1();
        assertEquals(expected, actual);
        verify(certificateRepository).findByNameIgnoreCase("FiRsT");
        verify(certificateMapper).toDto(CertificateUtil.certificateWithId1());
    }

    @Test
    void throwExceptionIfCertificateNameNotExist() {
        doReturn(Optional.empty())
                .when(certificateRepository).findByNameIgnoreCase("first");
        assertThrows(EntityNotFoundException.class, () -> certificateService.findByNameIgnoreCase("first"));
    }

    @Test
    void checkSaveIfCertificateHasUniqueName() {
        doReturn(TagUtil.tagDtoWithoutId())
                .when(tagService).saveOrUpdate(any(TagDto.class));
        doReturn(CertificateUtil.certificateWithoutId())
                .when(certificateMapper).toEntity(CertificateUtil.certificateDtoWithoutId());
        doReturn(CertificateUtil.certificateWithId1())
                .when(certificateRepository).save(CertificateUtil.certificateWithoutId());
        doReturn(CertificateUtil.certificateDtoWithId1())
                .when(certificateMapper).toDto(CertificateUtil.certificateWithId1());
        CertificateDto actual = certificateService.save(CertificateUtil.certificateDtoWithoutId());
        CertificateDto expected = CertificateUtil.certificateDtoWithId1();
        assertEquals(expected, actual);
    }

    @Test
    void throwExceptionBySaveIfCertificateHasNotUniqueName() {
        doReturn(TagUtil.tagDtoWithoutId())
                .when(tagService).saveOrUpdate(any(TagDto.class));
        doReturn(CertificateUtil.certificateWithoutId())
                .when(certificateMapper).toEntity(CertificateUtil.certificateDtoWithoutId());
        doThrow(DataIntegrityViolationException.class)
                .when(certificateRepository).save(CertificateUtil.certificateWithoutId());
        assertThrows(DataIntegrityViolationException.class, () -> certificateService.save(CertificateUtil.certificateDtoWithoutId()));
    }

    @Test
    void checkUpdateIfCertificateHasUniqueIdAndUniqueName() {
        doReturn(Optional.of(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findById(1);
        doNothing()
                .when(certificateMapper).updateDto(CertificateUtil.certificateDtoForUpdate(), CertificateUtil.certificateDtoWithId1());
        doReturn(TagUtil.tagDtoWithId1())
                .when(tagService).saveOrUpdate(TagUtil.tagDtoWithId1());
        doReturn(TagUtil.tagDtoWithId5())
                .when(tagService).saveOrUpdate(TagUtil.tagDtoWithId5());
        doReturn(CertificateUtil.certificateUpdated())
                .when(certificateMapper).toEntity(CertificateUtil.certificateDtoUpdated());
        doReturn(CertificateUtil.certificateUpdated())
                .when(certificateRepository).save(CertificateUtil.certificateUpdated());
        doReturn(CertificateUtil.certificateDtoUpdated())
                .when(certificateMapper).toDto(CertificateUtil.certificateUpdated());
        CertificateDto actual = certificateService.update(1, CertificateUtil.certificateDtoForUpdate());
        CertificateDto expected = CertificateUtil.certificateDtoUpdated();
        assertEquals(expected, actual);
        verify(certificateMapper).toEntity(CertificateUtil.certificateDtoUpdated());
        verify(certificateRepository).save(CertificateUtil.certificateUpdated());
    }

    @Test
    void throwExceptionByUpdateIfCertificateIdNotExist() {
        doReturn(Optional.empty())
                .when(certificateRepository).findById(1);
        assertThrows(EntityNotFoundException.class,
                () -> certificateService.update(1, CertificateUtil.certificateDtoForUpdateWithoutId()));
        verify(certificateRepository, never()).save(CertificateUtil.certificateForUpdateWithId());
    }

    @Test
    void checkUpdatePriceIfCertificateHasUniqueId() {
        doReturn(Optional.of(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findById(1);
        doNothing()
                .when(certificateMapper).updatePriceDto(CertificateUtil.certificatePriceDto(), CertificateUtil.certificateDtoWithId1());
        doReturn(CertificateUtil.certificateUpdatedPrice())
                .when(certificateMapper).toEntity(CertificateUtil.certificateDtoUpdatedPrice());
        doReturn(CertificateUtil.certificateUpdatedPrice())
                .when(certificateRepository).save(CertificateUtil.certificateUpdatedPrice());
        doReturn(CertificateUtil.certificateDtoUpdatedPrice())
                .when(certificateMapper).toDto(CertificateUtil.certificateUpdatedPrice());
        CertificateDto actual = certificateService.updatePrice(1, CertificateUtil.certificatePriceDto());
        CertificateDto expected = CertificateUtil.certificateDtoUpdatedPrice();
        assertEquals(expected, actual);
        verify(certificateMapper).toEntity(CertificateUtil.certificateDtoUpdatedPrice());
        verify(certificateRepository).save(CertificateUtil.certificateUpdatedPrice());
    }

    @Test
    void throwExceptionByUpdatePriceIfCertificateIdNotExist() {
        doReturn(Optional.empty())
                .when(certificateRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> certificateService.updatePrice(1, CertificateUtil.certificatePriceDto()));
        verify(certificateRepository, never()).save(CertificateUtil.certificateUpdatedPrice());
    }

    @Test
    void checkUpdateDurationIfCertificateHasUniqueId() {
        doReturn(Optional.of(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findById(1);
        doNothing()
                .when(certificateMapper).updateDurationDto(CertificateUtil.certificateDurationDto(), CertificateUtil.certificateDtoWithId1());
        doReturn(CertificateUtil.certificateUpdatedDuration())
                .when(certificateMapper).toEntity(CertificateUtil.certificateDtoUpdatedDuration());
        doReturn(CertificateUtil.certificateUpdatedDuration())
                .when(certificateRepository).save(CertificateUtil.certificateUpdatedDuration());
        doReturn(CertificateUtil.certificateDtoUpdatedDuration())
                .when(certificateMapper).toDto(CertificateUtil.certificateUpdatedDuration());
        CertificateDto actual = certificateService.updateDuration(1, CertificateUtil.certificateDurationDto());
        CertificateDto expected = CertificateUtil.certificateDtoUpdatedDuration();
        assertEquals(expected, actual);
        verify(certificateMapper).toEntity(CertificateUtil.certificateDtoUpdatedDuration());
        verify(certificateRepository).save(CertificateUtil.certificateUpdatedDuration());
    }

    @Test
    void throwExceptionByUpdateDurationIfCertificateIdNotExist() {
        doReturn(Optional.empty())
                .when(certificateRepository).findById(1);
        assertThrows(EntityNotFoundException.class,
                () -> certificateService.updateDuration(1, CertificateUtil.certificateDurationDto()));
        verify(certificateRepository, never()).save(CertificateUtil.certificateUpdatedDuration());
    }

    @Test
    void checkDeleteIfCertificateHasUniqueId() {
        doReturn(Optional.of(CertificateUtil.certificateWithId1()))
                .when(certificateRepository).findById(1);
        assertDoesNotThrow(() -> certificateService.delete(1));
        verify(certificateRepository).deleteById(1);
    }

    @Test
    void throwExceptionByDeleteIfCertificateIdNotExist() {
        doReturn(Optional.empty())
                .when(certificateRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> certificateService.delete(1));
        verify(certificateRepository, never()).deleteById(1);
    }
}
