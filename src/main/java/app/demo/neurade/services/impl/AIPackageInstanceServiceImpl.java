package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.AIPackageInstanceDTO;
import app.demo.neurade.domain.dtos.requests.TokenUsageLimit;
import app.demo.neurade.domain.dtos.requests.UserInstanceUsageCreationRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.domain.models.AIPackageInstance;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserAIInstanceUsage;
import app.demo.neurade.repositories.AIPackageInstanceRepository;
import app.demo.neurade.repositories.UserInstanceUsageRepository;
import app.demo.neurade.repositories.UserRepository;
import app.demo.neurade.services.AIPackageInstanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIPackageInstanceServiceImpl implements AIPackageInstanceService {
    private final AIPackageInstanceRepository instanceRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final UserInstanceUsageRepository userInstanceUsageRepository;

    @Override
    @Transactional
    public AIPackageInstanceDTO getInstanceById(UUID instanceId) {
        AIPackageInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("AI Package Instance not found with id: " + instanceId));

        AIPackage aiPackage = instance.getAiPackage();

        return mapper.toDto(instance, aiPackage);
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

        TokenUsageLimit limit = req.getTokenLimit();

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
}
