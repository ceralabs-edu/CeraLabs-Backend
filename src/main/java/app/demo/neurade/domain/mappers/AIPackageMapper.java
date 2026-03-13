package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.requests.AIPackageModificationRequest;
import app.demo.neurade.domain.models.AIPackage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AIPackageMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAIPackageFromDto(AIPackageModificationRequest dto, @MappingTarget AIPackage entity);
}
