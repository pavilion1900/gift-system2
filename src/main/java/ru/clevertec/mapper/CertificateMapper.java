package ru.clevertec.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.clevertec.dto.CertificateDto;
import ru.clevertec.dto.CertificateDurationDto;
import ru.clevertec.dto.CertificatePriceDto;
import ru.clevertec.entity.Certificate;

import java.time.LocalDateTime;

@Mapper(uses = {TagMapper.class}, imports = LocalDateTime.class)
public interface CertificateMapper {

    @Mapping(source = "certificate.tags", target = "dtoTags")
    CertificateDto toDto(Certificate certificate);

    @Mapping(source = "certificateDto.dtoTags", target = "tags")
    @Mapping(target = "createDate", defaultExpression = "java(LocalDateTime.now())")
    @Mapping(target = "lastUpdateDate", defaultExpression = "java(LocalDateTime.now())")
    Certificate toEntity(CertificateDto certificateDto);

    @Mapping(target = "lastUpdateDate", expression = "java(LocalDateTime.now())")
    void updateDto(CertificateDto source, @MappingTarget CertificateDto target);

    @Mapping(target = "lastUpdateDate", expression = "java(LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "dtoTags", ignore = true)
    void updatePriceDto(CertificatePriceDto source, @MappingTarget CertificateDto target);

    @Mapping(target = "lastUpdateDate", expression = "java(LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "dtoTags", ignore = true)
    void updateDurationDto(CertificateDurationDto source, @MappingTarget CertificateDto target);
}
