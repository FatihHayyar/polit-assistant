package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("docker")
public class DockerBootstrapService implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(DockerBootstrapService.class);

    private final AffairReadJpaRepository affairRepository;
    private final AffairImportService affairImportService;
    private final RuleBasedClassificationService classificationService;

    private final boolean enabled;
    private final int affairLimit;

    public DockerBootstrapService(
            AffairReadJpaRepository affairRepository,
            AffairImportService affairImportService,
            RuleBasedClassificationService classificationService,
            @Value("${app.bootstrap.enabled:true}") boolean enabled,
            @Value("${app.bootstrap.affair-limit:50}") int affairLimit
    ) {
        this.affairRepository = affairRepository;
        this.affairImportService = affairImportService;
        this.classificationService = classificationService;
        this.enabled = enabled;
        this.affairLimit = affairLimit;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Docker bootstrap is disabled.");
            return;
        }

        long existingAffairs = affairRepository.count();

        if (existingAffairs > 0) {
            log.info(
                    "Docker bootstrap skipped: database already contains {} affairs.",
                    existingAffairs
            );
            return;
        }

        if (affairLimit <= 0) {
            log.warn(
                    "Docker bootstrap skipped: affairLimit must be greater than 0."
            );
            return;
        }

        log.info(
                "Empty database detected. Starting Docker bootstrap with {} affairs.",
                affairLimit
        );

        try {
            var result =
                    affairImportService.importAffairsWithDocsOnly(
                            0,
                            affairLimit
                    );

            int classified = 0;

            if (!result.importedAffairIds().isEmpty()) {
                classified =
                        classificationService.classifyAll(
                                result.importedAffairIds()
                        );
            }

            log.info(
                    "Docker bootstrap completed: affairs={}, documents={}, classified={}.",
                    result.affairsImported(),
                    result.docsImported(),
                    classified
            );

        } catch (Exception exception) {
            /*
             * Bootstrap data is convenient for local/demo installations,
             * but an unavailable external API must not prevent the
             * application itself from starting.
             */
            log.error(
                    "Docker bootstrap failed. Application will continue without bootstrap data. Reason: {}",
                    exception.getMessage(),
                    exception
            );
        }
    }
}