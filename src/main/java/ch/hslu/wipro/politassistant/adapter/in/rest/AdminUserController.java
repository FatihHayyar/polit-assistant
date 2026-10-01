package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(
        name = "Admin Users",
        description = "Administration der Polit-Assistant-Benutzer"
)
public class AdminUserController {

    private final AdminUserService service;

    public AdminUserController(
            AdminUserService service
    ) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Alle Benutzer anzeigen"
    )
    public List<AdminUserService.AdminUserResponse>
    getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Benutzer anzeigen"
    )
    public AdminUserService.AdminUserResponse getUser(
            @PathVariable UUID id
    ) {
        return service.getUser(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Benutzer aktualisieren"
    )
    public AdminUserService.AdminUserResponse updateUser(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            AdminUserService.AdminUserUpdateRequest request
    ) {
        return service.updateUser(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Benutzer vollständig löschen"
    )
    public AdminUserService.AdminDeleteResponse deleteUser(
            @PathVariable UUID id
    ) {
        return service.deleteUser(id);
    }
}