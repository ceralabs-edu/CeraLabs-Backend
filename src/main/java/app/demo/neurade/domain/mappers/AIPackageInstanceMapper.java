package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.requests.ModifyAIPackageInstanceRequest;
import app.demo.neurade.domain.models.AIPackageInstance;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AIPackageInstanceMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchInstance(ModifyAIPackageInstanceRequest req, @MappingTarget AIPackageInstance instance);
}

