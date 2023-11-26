package me.alsturm.reminder.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EnvironmentPropertiesPrinter {

    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Print properties from command line and application properties from classpath.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void printProperties() {
        String printableProperties = env.getPropertySources().stream()
            .filter(this::isNameCorrect)
            .filter(EnumerablePropertySource.class::isInstance)
            .map(EnumerablePropertySource.class::cast)
            .map(EnumerablePropertySource::getPropertyNames)
            .flatMap(Arrays::stream)
            .distinct()
            .map(this::formatKeyValue)
            .collect(Collectors.joining(System.lineSeparator()));
        log.info("Environment properties:{}{}", System.lineSeparator(), printableProperties);
    }

    /**
     * We need to check only properties from .properties file and command line,
     * therefore we check name of the source
     */
    private boolean isNameCorrect(PropertySource<?> propertySource) {
        return propertySource.getName().contains(".properties")
            || propertySource.getName().contains(".yaml")
            || propertySource.getName().contains(".yml")
            || propertySource.getName().contains("commandLine");
    }

    private String formatKeyValue(String key) {
        String value = sanitize(key);
        return key + "=" + value;
    }

    /**
     * Hide passwords
     */
    private String sanitize(String key) {
        if (key == null) {
            return null;
        } else {
            if (key.contains("passw") || key.contains("token")) {
                return "<hidden>";
            } else {
                return env.getProperty(key);
            }
        }
    }
}
