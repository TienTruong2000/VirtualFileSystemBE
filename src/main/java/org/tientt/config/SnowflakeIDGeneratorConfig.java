package org.tientt.config;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class SnowflakeIDGeneratorConfig {
    @Value("${snowflake.generator.id}")
    private int generatorId;

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return SnowflakeIdGenerator.createDefault(generatorId);
    }
}
