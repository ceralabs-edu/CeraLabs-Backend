package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.request.AIPackageCreationRequest;
import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.domain.models.AIPackageInstance;
import app.demo.neurade.domain.models.User;

public interface AIPackageService {
    AIPackage createPackage(AIPackageCreationRequest req);
    AIPackageInstance purchasePackage(User buyer, Long classId, Integer aiPackageId);
}
