package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.AIPackageInstanceDTO;
import app.demo.neurade.domain.dtos.requests.TokenUsageLimit;
import app.demo.neurade.domain.dtos.requests.UserInstanceUsageCreationRequest;
import app.demo.neurade.domain.dtos.requests.ModifyAIPackageInstanceRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.domain.models.AIPackageInstance;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserAIInstanceUsage;
import app.demo.neurade.infrastructures.repositories.AIPackageInstanceRepository;
import app.demo.neurade.infrastructures.repositories.AIPackageRepository;
import app.demo.neurade.infrastructures.repositories.ParticipantRepository;
import app.demo.neurade.infrastructures.repositories.UserInstanceUsageRepository;
import app.demo.neurade.infrastructures.repositories.UserRepository;
import app.demo.neurade.services.AIPackageInstanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIPackageInstanceServiceImpl implements AIPackageInstanceService {

    private static final String FREE_PACKAGE_NAME = "Starter AI Package";

    private final AIPackageInstanceRepository instanceRepository;
    private final AIPackageRepository aiPackageRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final UserInstanceUsageRepository userInstanceUsageRepository;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional
    public AIPackageInstanceDTO getInstanceById(UUID instanceId) {
        AIPackageInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("AI Package Instance not found with id: " + instanceId));
        return mapper.toDto(instance);
    }

    @Override
    @Transactional
    public UserAIInstanceUsage createUsageRecord(UserInstanceUsageCreationRequest req) {
        AIPackageInstance instance = instanceRepository.findById(req.getInstanceId())
                .orElseThrow(() -> new RuntimeException("AI Package Instance not found with id: " + req.getInstanceId()));

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + req.getUserId()));

        AIPackage aiPackage = instance.getAiPackage();

        var builder = UserAIInstanceUsage.builder()
                .user(user)
                .tokenUsed(0L)
                .instance(instance)
                .limitToken(aiPackage.getTotalToken());

        TokenUsageLimit limit = req.getUsageLimit();

        if (limit != null) {
            builder = builder
                    .limitToken(limit.getTokenLimit())
                    .rateLimitTokenLimit(limit.getRateLimitTokenMax())
                    .rateLimitTokenCount(0L)
                    .rateLimitDurationDays(limit.getDurationInDays());
        }

        var userAIInstanceUsage = builder.build();

        log.info("Creating usage for user {} on instance {} with limits: {}",
                user.getId(), instance.getId(), limit);

        userAIInstanceUsage = userInstanceUsageRepository.save(builder.build());

        log.info("Done creating usage");
        return userAIInstanceUsage;
    }

    @Override
    public AIPackageInstanceDTO getInstanceForUser(Long userId, Long classId) {
        if (classId == null) {
            List<AIPackageInstance> instances = instanceRepository.findPersonalInstance(userId);
            if (instances.isEmpty()) {
                throw new RuntimeException("No personal AI Package Instance found for user id: " + userId);
            }
            if (instances.size() > 1) {
                log.warn("User {} has multiple personal AI Package Instances, returning the first one", userId);
            }
            return mapper.toDto(instances.getFirst());
        }
        if (!participantRepository.existsByClazz_IdAndUser_Id(classId, userId)) {
            throw new RuntimeException("User is not a participant of the class");
        }

        AIPackageInstance instance = instanceRepository.findByClassRoom_Id(classId)
                .orElseThrow(() -> new RuntimeException("No classroom AI Package Instance found for class id: " + classId));

        if (!userInstanceUsageRepository.existsByUser_IdAndInstance_Id(userId, instance.getId())) {
            throw new RuntimeException("User does not have usage record for the instance");
        }

        return mapper.toDto(instance);
    }

    @Override
    @Transactional
    public void createFreeInstanceForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        AIPackage freePackage = aiPackageRepository.findByName(FREE_PACKAGE_NAME)
                .orElseThrow(() -> new RuntimeException(
                        "Free AI package '" + FREE_PACKAGE_NAME + "' not found. " +
                        "Make sure it is seeded in the database."
                ));

        log.info("Creating free AI instance '{}' for user: {}", FREE_PACKAGE_NAME, user.getEmail());

        AIPackageInstance instance = AIPackageInstance.builder()
                .aiPackage(freePackage)
                .buyer(user)
                .purchaseDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(freePackage.getDurationInDays()))
                .remainingToken(freePackage.getTotalToken())
                .build();

        instance = instanceRepository.save(instance);

        log.info("Free AI instance created with ID: {} for user: {}", instance.getId(), user.getEmail());

        createUsageRecord(UserInstanceUsageCreationRequest.builder()
                .instanceId(instance.getId())
                .userId(userId)
                .build());

        log.info("Usage record created for free AI instance for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public AIPackageInstanceDTO modifyInstance(UUID instanceId, ModifyAIPackageInstanceRequest req) {
        AIPackageInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("AI Package Instance not found with id: " + instanceId));

        if (req.getAiPackageId() != null) {
            AIPackage aiPackage = aiPackageRepository.findById(req.getAiPackageId())
                    .orElseThrow(() -> new RuntimeException("AI Package not found with id: " + req.getAiPackageId()));
            instance.setAiPackage(aiPackage);
        }
        if (req.getExpiryDate() != null) {
            instance.setExpiryDate(req.getExpiryDate());
        }
        if (req.getRemainingToken() != null) {
            instance.setRemainingToken(req.getRemainingToken().longValue());
        }
        // Add more fields as needed

        instanceRepository.save(instance);
        return mapper.toDto(instance);
    }
}
