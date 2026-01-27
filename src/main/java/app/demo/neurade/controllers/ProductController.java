package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.request.AIPackageCreationRequest;
import app.demo.neurade.domain.dtos.request.AIPackagePurchaseRequest;
import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.domain.models.AIPackageInstance;
import app.demo.neurade.security.CustomUserDetails;
import app.demo.neurade.services.AIPackageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Product management APIs")
public class ProductController {

    private final AIPackageService aiPackageService;

    @PostMapping("/ai-package")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAIPackage(@RequestBody AIPackageCreationRequest req) {
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
        AIPackageInstance instance = aiPackageService.purchasePackage(userDetails.getUser(), req.getClassId(), req.getAiPackageId());
        return ResponseEntity.ok(
                Map.of(
                        "message", "AI Package purchased successfully"
                )
        );
    }
}
