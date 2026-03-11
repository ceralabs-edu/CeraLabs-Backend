package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.AIPackageInstanceDTO;
import app.demo.neurade.domain.dtos.requests.UserInstanceUsageCreationRequest;
import app.demo.neurade.domain.dtos.requests.ModifyAIPackageInstanceRequest;
import app.demo.neurade.domain.models.UserAIInstanceUsage;
import java.util.UUID;

public interface AIPackageInstanceService {
    AIPackageInstanceDTO getInstanceById(UUID instanceId);
    UserAIInstanceUsage createUsageRecord(UserInstanceUsageCreationRequest req);
    AIPackageInstanceDTO getInstanceForUser(Long userId, Long classId);
    void createFreeInstanceForUser(Long userId);
    AIPackageInstanceDTO modifyInstance(UUID instanceId, ModifyAIPackageInstanceRequest req);
}
