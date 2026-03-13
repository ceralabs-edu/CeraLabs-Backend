package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.JwtAccessTokenDTO;
import app.demo.neurade.domain.models.JwtAccessToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JwtAccessTokenMapper {
    @Mapping(target = "userId", source = "user.id")
    JwtAccessTokenDTO toDto(JwtAccessToken token);
}

