package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.ClassDTO;
import app.demo.neurade.domain.dtos.request.ClassCreationRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.security.CustomUserDetails;
import app.demo.neurade.services.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/class")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
@Tag(
        name = "Class",
        description = "APIs for managing classes"
)
public class ClassController {

    private final ClassService classService;
    private final Mapper mapper;

    @Operation(
            summary = "Create a new class",
            description = """
            Create a new classroom.
            
            Permissions:
            - ADMIN
            - TEACHER
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Class created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClassDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid JWT"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient role"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create")
    public ResponseEntity<?> createClass(
            @RequestBody ClassCreationRequest req
    ) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        Classroom newClass =
                classService.createClass(userDetails.getUser(), req);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Class created successfully",
                        "data", mapper.toDto(newClass)
                )
        );
    }
}
