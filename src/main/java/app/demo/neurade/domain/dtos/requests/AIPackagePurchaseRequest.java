package app.demo.neurade.domain.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPackagePurchaseRequest {
    private Long classId;
    private Integer aiPackageId;
}
