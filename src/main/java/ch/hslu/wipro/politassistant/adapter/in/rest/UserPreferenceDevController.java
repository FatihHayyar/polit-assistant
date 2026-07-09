package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dev/users/preferences")
@Tag(name = "User Preferences", description = "Manage user topic preferences")
class UserPreferenceDevController {

    private final UserPreferenceService service;

    UserPreferenceDevController(UserPreferenceService service) {
        this.service = service;
    }

    @PostMapping
    void addPreference(@RequestBody AddPreferenceRequest request) {
        service.addPreference(
                request.email(),
                request.displayName(),
                request.topic(),
                request.channel()
        );
    }

    record AddPreferenceRequest(
            String email,
            String displayName,
            String topic,
            String channel
    ) {}
    @GetMapping
    java.util.List<UserPreferenceService.UserPreferenceResponse> listPreferences() {
        return service.listPreferences();
    }
}