package app.demo.neurade.domain.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAndInfoDTO {
    private UserDTO user;
    private UserInfoDTO info;
}
