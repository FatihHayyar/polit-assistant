package ch.hslu.wipro.politassistant.config;

import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "classification")
public class ClassificationRulesProperties {

    private Map<Topic, List<String>> rules;

    public Map<Topic, List<String>> getRules() {
        return rules;
    }

    public void setRules(Map<Topic, List<String>> rules) {
        this.rules = rules;
    }
}