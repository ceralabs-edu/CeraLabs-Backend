package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.requests.PatchUserRequest;
import app.demo.neurade.domain.models.UserInformation;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserInformationMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUserInfo(PatchUserRequest req, @MappingTarget UserInformation info);
}
