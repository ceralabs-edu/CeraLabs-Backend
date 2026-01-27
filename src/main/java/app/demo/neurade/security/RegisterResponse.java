package app.demo.neurade.security;

import app.demo.neurade.domain.dtos.UserDTO;
import lombok.*;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class RegisterResponse {
    private UserDTO user;
    private String message;
}
