package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.user.AppUserEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.AppUserJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPreferenceService {

    private final AppUserJpaRepository userRepository;
    private final UserPreferenceJpaRepository preferenceRepository;

    public UserPreferenceService(
            AppUserJpaRepository userRepository,
            UserPreferenceJpaRepository preferenceRepository
    ) {
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional
    public void addPreference(String email, String displayName, String topic, String channel) {
        if (preferenceRepository.existsByUserEmailAndTopicAndChannel(email, topic, channel)) {
            return;
        }

        AppUserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(AppUserEntity.create(email, displayName)));

        preferenceRepository.save(UserPreferenceEntity.create(user, topic, channel));
    }
    @Transactional(readOnly = true)
    public java.util.List<UserPreferenceResponse> listPreferences() {
        return preferenceRepository.findAll()
                .stream()
                .map(preference -> new UserPreferenceResponse(
                        preference.getEmail(),
                        preference.getUser().getDisplayName(),
                        preference.getTopic(),
                        preference.getChannel(),
                        preference.isActive()
                ))
                .toList();
    }

    public record UserPreferenceResponse(
            String email,
            String displayName,
            String topic,
            String channel,
            boolean active
    ) {}
}