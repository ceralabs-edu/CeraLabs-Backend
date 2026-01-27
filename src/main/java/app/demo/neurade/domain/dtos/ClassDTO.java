package app.demo.neurade.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClassDTO {
    private Long classId;
    private Long creatorId;
    private String name;
    private String description;
}
