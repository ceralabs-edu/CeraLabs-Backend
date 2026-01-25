package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.UserDTO;
import app.demo.neurade.domain.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {
    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .verified(user.getVerified())
                .build();
    }
}
