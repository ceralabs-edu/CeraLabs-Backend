package app.demo.neurade.domain.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassDTO {
    private Long classId;
    private Long creatorId;
    private String name;
    private String description;
}
