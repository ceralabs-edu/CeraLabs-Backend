package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.AIPackageInstanceDTO;
import app.demo.neurade.domain.dtos.ValidateKeyDTO;
import app.demo.neurade.infrastructures.chatbot_llm.requests.VerifyKeyRequest;
import app.demo.neurade.infrastructures.chatbot_llm.responses.VerifyKeyResponse;
import app.demo.neurade.domain.dtos.requests.AIPackageCreationRequest;
import app.demo.neurade.domain.dtos.requests.UserInstanceUsageCreationRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.*;
import app.demo.neurade.infrastructures.repositories.AIPackageInstanceRepository;
import app.demo.neurade.infrastructures.repositories.AIPackageRepository;
import app.demo.neurade.infrastructures.repositories.ClassRepository;
import app.demo.neurade.infrastructures.repositories.PeopleManagementRepository;
import app.demo.neurade.services.AIPackageInstanceService;
import app.demo.neurade.services.AIPackageService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIPackageServiceImpl implements AIPackageService {

    private final AIPackageRepository aiPackageRepository;
    private final ClassRepository classroomRepository;
    private final PeopleManagementRepository peopleManagementRepository;
    private final AIPackageInstanceRepository aiPackageInstanceRepository;
    private final RestTemplate restTemplate;
    private final Mapper mapper;
    private final EntityManager entityManager;
    private final AIPackageInstanceService aIPackageInstanceService;

    @Value("${llm.validate.endpoint}")
    private String verificationEndpoint;

    @Override
    @Transactional
    public AIPackage createPackage(AIPackageCreationRequest req) {
        AIPackage aiPackage = AIPackage.builder()
                .name(req.getName())
                .price(req.getPrice())
                .totalToken(req.getTotalToken())
                .tokenRateLimit(req.getTokenRateLimit())
                .model(req.getModel())
                .durationInDays(req.getDurationInDays())
                .description(req.getDescription())
                .apiKeys(req.getApiKeys())
                .build();

        return aiPackageRepository.save(aiPackage);
    }

    @Override
    @Transactional
    public AIPackageInstanceDTO purchasePackage(User buyer, Long classId, Integer aiPackageId) {
        AIPackageInstanceDTO dto = (classId == null) ?
                purchasePackageForUser(buyer, aiPackageId) :
                purchasePackageForClass(buyer, classId, aiPackageId);

        aIPackageInstanceService.createUsageRecord(
                UserInstanceUsageCreationRequest.builder()
                        .instanceId(dto.getInstanceId())
                        .userId(buyer.getId())
                        .build()
        );

        return dto;
    }

    @Transactional
    public AIPackageInstanceDTO purchasePackageForUser(User buyer, Integer aiPackageId) {
        AIPackage aiPackage = aiPackageRepository.findById(aiPackageId)
                .orElseThrow(() -> new RuntimeException("AI Package not found with id: " + aiPackageId));

        log.info("Purchasing AI Package: {} for User: {}", aiPackage.getName(), buyer.getEmail());

        int existed = aiPackageInstanceRepository.deletePersonalInstance(buyer.getId());

        log.info("User {} had {} existing AI Package instances removed", buyer.getEmail(), existed);

        entityManager.flush();

        log.info("EntityManager flushed to ensure deletion is executed before creating new instance");

        AIPackageInstance aiPackageInstance = AIPackageInstance.builder()
                .aiPackage(aiPackage)
                .purchaseDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(aiPackage.getDurationInDays()))
                .remainingToken(aiPackage.getTotalToken())
                .buyer(buyer)
                .build();

        aiPackageInstance = aiPackageInstanceRepository.save(aiPackageInstance);

        log.info("AI Package Instance created with ID: {}", aiPackageInstance.getId());

        return mapper.toDto(aiPackageInstance);
    }


    public AIPackageInstanceDTO purchasePackageForClass(User buyer, Long classId, Integer aiPackageId) {
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


        log.info("Purchasing AI Package: {} for Class: {} by User: {}", aiPackage.getName(), classId, buyer.getEmail());

        int existed = aiPackageInstanceRepository.deleteClassroomInstance(classroom.getId());

        log.info("Class {} had {} existing AI Package instances removed", classId, existed);

        entityManager.flush();

        log.info("EntityManager flushed to ensure deletion is executed before creating new instance");

        AIPackageInstance aiPackageInstance = AIPackageInstance.builder()
                .aiPackage(aiPackage)
                .classRoom(classroom)
                .purchaseDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(aiPackage.getDurationInDays()))
                .remainingToken(aiPackage.getTotalToken())
                .buyer(buyer)
                .build();

        aiPackageInstance = aiPackageInstanceRepository.save(aiPackageInstance);

        log.info("AI Package Instance created with ID: {}", aiPackageInstance.getId());

        return mapper.toDto(aiPackageInstance);
    }

    @Override
    public ValidateKeyDTO validateApiKey(String apiKey, String provider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        VerifyKeyRequest extRequest = VerifyKeyRequest.builder()
                .apiKey(apiKey)
                .provider(provider)
                .build();

        log.info("Sending API key validation request to external service: {}", verificationEndpoint);

        VerifyKeyResponse extResponse = restTemplate.postForObject(verificationEndpoint, extRequest, VerifyKeyResponse.class);
        if (extResponse == null) {
            throw new RuntimeException("Failed to validate API key");
        }

        log.info("Received API key validation response: {}", extResponse);
        return mapper.toDto(extResponse);
    }

    @Override
    public AIPackage getPackageById(Integer packageId) {
        return aiPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("AI Package not found with id: " + packageId));
    }

    @Override
    public List<AIPackage> getAllPackages() {
        return aiPackageRepository.findAll();
    }
}
