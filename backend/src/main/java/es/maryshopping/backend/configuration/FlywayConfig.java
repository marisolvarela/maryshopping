package es.maryshopping.backend.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
@Getter
@Setter
@Slf4j
@ConfigurationProperties(prefix = "flywayconf")
public class FlywayConfig {
    private ModuleConfig customers = new ModuleConfig();
    private ModuleConfig orders = new ModuleConfig();
    private ModuleConfig catalog = new ModuleConfig();
    private ModuleConfig reports = new ModuleConfig();

    @Getter
    @Setter
    public static class ModuleConfig {
        private List<String> schemas = new ArrayList<>();
        private List<String> locations = new ArrayList<>();

        public Map<String, String[]> toMap() {
            Map<String, String[]> map = new HashMap<>();
            map.put("schemas", schemas.toArray(new String[0]));
            map.put("locations", locations.toArray(new String[0]));
            return map;
        }
    }
}

