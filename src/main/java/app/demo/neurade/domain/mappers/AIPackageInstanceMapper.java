package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.requests.ModifyAIPackageInstanceRequest;
import app.demo.neurade.domain.models.AIPackageInstance;
import org.mapstruct.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AIPackageInstanceMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchInstance(ModifyAIPackageInstanceRequest req, @MappingTarget AIPackageInstance instance);
}

