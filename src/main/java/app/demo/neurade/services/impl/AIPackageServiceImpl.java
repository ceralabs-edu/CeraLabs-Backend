package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.request.AIPackageCreationRequest;
import app.demo.neurade.domain.models.*;
import app.demo.neurade.repositories.AIPackageInstanceRepository;
import app.demo.neurade.repositories.AIPackageRepository;
import app.demo.neurade.repositories.ClassRepository;
import app.demo.neurade.repositories.PeopleManagementRepository;
import app.demo.neurade.services.AIPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AIPackageServiceImpl implements AIPackageService {

    private final AIPackageRepository aiPackageRepository;
    private final ClassRepository classroomRepository;
    private final PeopleManagementRepository peopleManagementRepository;
    private final AIPackageInstanceRepository aiPackageInstanceRepository;

    @Override
    public AIPackage createPackage(AIPackageCreationRequest req) {
        AIPackage aiPackage = AIPackage.builder()
                .name(req.getName())
                .price(req.getPrice())
                .totalToken(req.getTotalToken())
                .tokenRateLimit(req.getTokenRateLimit())
                .model(req.getModel())
                .durationInDays(req.getDurationInDays())
                .build();

        return aiPackageRepository.save(aiPackage);
    }

    @Override
    public AIPackageInstance purchasePackage(User buyer, Long classId, Integer aiPackageId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classId));

        boolean canPurchase = false;

        if (buyer.getRole().isRoleType(RoleType.ADMIN)) {
            canPurchase = true;
        } else if (buyer.getRole().isRoleType(RoleType.ORGANIZATION)) {
            canPurchase = peopleManagementRepository.existsByManagerIdAndManagedId(
                    buyer.getId(),
                    classroom.getCreator().getId()
            );
        } else if (buyer.getRole().isRoleType(RoleType.TEACHER)) {
            canPurchase = buyer.getId().equals(classroom.getCreator().getId());
        }

        if (!canPurchase) {
            throw new RuntimeException("User does not have permission to purchase AI Package for this class");
        }

        AIPackage aiPackage = aiPackageRepository.findById(aiPackageId)
                .orElseThrow(() -> new RuntimeException("AI Package not found with id: " + aiPackageId));

        AIPackageInstance aiPackageInstance = AIPackageInstance.builder()
                .aiPackage(aiPackage)
                .classRoom(classroom)
                .purchaseDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(aiPackage.getDurationInDays()))
                .usedToken(0L)
                .buyer(buyer)
                .build();

        return aiPackageInstanceRepository.save(aiPackageInstance);
    }
}
