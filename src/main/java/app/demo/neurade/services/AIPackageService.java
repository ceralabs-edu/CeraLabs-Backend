package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.AIPackageInstanceDTO;
import app.demo.neurade.domain.dtos.ValidateKeyDTO;
import app.demo.neurade.domain.dtos.requests.AIPackageCreationRequest;
import app.demo.neurade.domain.dtos.requests.AIPackageModificationRequest;
import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.domain.models.User;

import java.util.List;

public interface AIPackageService {
    AIPackage createPackage(AIPackageCreationRequest req);
    AIPackageInstanceDTO purchasePackage(User buyer, Long classId, Integer aiPackageId);
    ValidateKeyDTO validateApiKey(String apiKey, String provider);
    AIPackage getPackageById(Integer packageId);
    List<AIPackage> getAllPackages();
    void modifyPackage(Integer packageId, AIPackageModificationRequest req);
    AIPackage setInactive(Integer packageId);
}
