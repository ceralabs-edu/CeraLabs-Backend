package app.demo.neurade.domain.dtos.requests;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetInstanceForUserRequest {
    private @Nullable Long classId;
}
