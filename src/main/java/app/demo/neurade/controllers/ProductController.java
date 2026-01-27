package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.requests.AIPackageCreationRequest;
import app.demo.neurade.domain.dtos.requests.AIPackagePurchaseRequest;
import app.demo.neurade.domain.dtos.requests.ValidateKeyRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.security.CustomUserDetails;
import app.demo.neurade.services.AIPackageInstanceService;
import app.demo.neurade.services.AIPackageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Product management APIs")
public class ProductController {

    private final AIPackageService aiPackageService;
    private final Mapper mapper;
    private final AIPackageInstanceService aIPackageInstanceService;

    @PostMapping("ai-model/api-key/validate")
    public ResponseEntity<?> getAIModels(@Valid @RequestBody ValidateKeyRequest req) {
        return ResponseEntity.ok(
                aiPackageService.validateApiKey(req.getApiKey(), req.getProvider())
        );
    }

    @PostMapping("/ai-package")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAIPackage(@Valid @RequestBody AIPackageCreationRequest req) {
        AIPackage aiPackage = aiPackageService.createPackage(req);
        return ResponseEntity.ok(
                Map.of(
                        "message", "AI Package created successfully",
                        "data", aiPackage
                )
        );
    }

    @PostMapping("/ai-package/purchase")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION', 'TEACHER')" )
    public ResponseEntity<?> purchaseAIPackage(@RequestBody AIPackagePurchaseRequest req) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(
                Map.of(
                        "message", "AI Package purchased successfully",
                        "data", aiPackageService.purchasePackage(
                                userDetails.getUser(),
                                req.getClassId(),
                                req.getAiPackageId()
                        )
                )
        );
    }

    @GetMapping("/ai-packages")
    public ResponseEntity<?> getAllAIPackages() {
        return ResponseEntity.ok(
                aiPackageService.getAllPackages()
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/ai-package/{packageId}")
    public ResponseEntity<?> getAIPackageById(@PathVariable Integer packageId) {
        AIPackage aiPackage = aiPackageService.getPackageById(packageId);
        return ResponseEntity.ok(
                mapper.toDto(aiPackage)
        );
    }

    @GetMapping("/ai-package/instance/{instanceId}")
    public ResponseEntity<?> getAIPackageInstanceById(@PathVariable String instanceId) {
        return ResponseEntity.ok(
                aIPackageInstanceService.getInstanceById(java.util.UUID.fromString(instanceId))
        );
    }
}
