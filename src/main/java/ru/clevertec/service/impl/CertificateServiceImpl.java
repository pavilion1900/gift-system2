package ru.clevertec.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.dto.CertificateDto;
import ru.clevertec.dto.CertificateDurationDto;
import ru.clevertec.dto.CertificatePriceDto;
import ru.clevertec.dto.TagDto;
import ru.clevertec.entity.Certificate;
import ru.clevertec.exception.EntityNotFoundException;
import ru.clevertec.mapper.CertificateMapper;
import ru.clevertec.repository.CertificateRepository;
import ru.clevertec.service.CertificateService;
import ru.clevertec.service.TagService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final TagService tagService;
    private final CertificateMapper certificateMapper;

    @Override
    public List<CertificateDto> findAll(Pageable pageable) {
        return certificateRepository.findAll(pageable).stream()
                .map(certificateMapper::toDto)
                .collect(toList());
    }

    @Override
    public List<CertificateDto> findAllByIgnoreCase(String name, String description, Pageable pageable) {
        return certificateRepository.findAll(
                        Example.of(Certificate.builder()
                                        .name(name)
                                        .description(description)
                                        .build(),
                                matcher()), pageable).stream()
                .map(certificateMapper::toDto)
                .collect(toList());
    }

    @Override
    public List<CertificateDto> findAllByTagName(String tagName, Pageable pageable) {
        return certificateRepository.findAllByTagName(tagName, pageable).stream()
                .map(certificateMapper::toDto)
                .collect(toList());
    }

    @Override
    public List<CertificateDto> findAllBySeveralTagNames(List<String> tagNames, Pageable pageable) {
        return certificateRepository.findAllBySeveralTagNames(tagNames, pageable).stream()
                .map(certificateMapper::toDto)
                .collect(toList());
    }

    @Override
    public CertificateDto findById(Integer id) {
        return certificateRepository.findById(id)
                .map(certificateMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Certificate with id %d not found", id)));
    }

    @Override
    public CertificateDto findByNameIgnoreCase(String certificateName) {
        return certificateRepository.findByNameIgnoreCase(certificateName)
                .map(certificateMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Certificate with name %s not found", certificateName)
                ));
    }

    @Override
    public Integer findLastSequenceValue() {
        return certificateRepository.findLastSequenceValue();
    }

    @Override
    @Transactional
    public CertificateDto save(CertificateDto certificateDto) {
        checkTags(certificateDto);
        Certificate certificate = certificateMapper.toEntity(certificateDto);
        return certificateMapper.toDto(certificateRepository.save(certificate));
    }

    @Override
    @Transactional
    public CertificateDto update(Integer id, CertificateDto certificateDto) {
        return certificateRepository.findById(id)
                .map(certificateMapper::toDto)
                .map(certificateDtoWithId -> {
                    certificateMapper.updateDto(certificateDto, certificateDtoWithId);
                    certificateDtoWithId.setId(id);
                    checkTags(certificateDtoWithId);
                    return certificateDtoWithId;
                })
                .map(certificateMapper::toEntity)
                .map(certificateRepository::save)
                .map(certificateMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Certificate with id %d not found", id)));
    }

    @Override
    @Transactional
    public CertificateDto updatePrice(Integer id, CertificatePriceDto certificatePriceDto) {
        return certificateRepository.findById(id)
                .map(certificateMapper::toDto)
                .map(certificateDtoWithId -> {
                    certificateMapper.updatePriceDto(certificatePriceDto, certificateDtoWithId);
                    return certificateDtoWithId;
                })
                .map(certificateMapper::toEntity)
                .map(certificateRepository::save)
                .map(certificateMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Certificate with id %d not found", id)));
    }

    @Override
    @Transactional
    public CertificateDto updateDuration(Integer id, CertificateDurationDto certificateDurationDto) {
        return certificateRepository.findById(id)
                .map(certificateMapper::toDto)
                .map(certificateDtoWithId -> {
                    certificateMapper.updateDurationDto(certificateDurationDto, certificateDtoWithId);
                    return certificateDtoWithId;
                })
                .map(certificateMapper::toEntity)
                .map(certificateRepository::save)
                .map(certificateMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Certificate with id %d not found", id)));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        certificateRepository.findById(id)
                .map(certificate -> {
                    certificateRepository.deleteById(id);
                    return certificate;
                })
                .orElseThrow(() -> new EntityNotFoundException(String.format("Certificate with id %d not exist", id)));
    }

    private void checkTags(CertificateDto certificateDto) {
        List<TagDto> tags = certificateDto.getDtoTags();
        tags.forEach(tagDto -> {
            Integer id = tagService.saveOrUpdate(tagDto).getId();
            tagDto.setId(id);
        });
        certificateDto.setDtoTags(tags);
    }

    private ExampleMatcher matcher() {
        return ExampleMatcher.matchingAny()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
    }
}
