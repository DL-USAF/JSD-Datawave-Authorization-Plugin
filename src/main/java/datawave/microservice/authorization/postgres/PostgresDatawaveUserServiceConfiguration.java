package datawave.microservice.authorization.postgres;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import datawave.microservice.cached.CacheInspector;

/**
 * Configuration to supply beans for the {@link PostgresDatawaveUserService}. This configuration is only active when the "jsd" profile is selected. This profile
 * is used for retrieving entity information from JSD cache
 */
@Configuration
@EnableCaching
@Profile("postgres")
public class PostgresDatawaveUserServiceConfiguration {
    @Bean
    public PostgresDatawaveUserService jsdDatawaveUserService(PostgresDatawaveUserLookup jsdDatawaveUserLookup, CacheManager cacheManager,
                    @Qualifier("cacheInspectorFactory") Function<CacheManager,CacheInspector> cacheInspectorFactory) {
        return new PostgresDatawaveUserService(jsdDatawaveUserLookup, cacheInspectorFactory.apply(cacheManager));
    }
    
    @Bean
    public PostgresDatawaveUserLookup jsdDatawaveUserLookup(PostgresDULProperties jsdDULProperties) {
        return new PostgresDatawaveUserLookup(jsdDULProperties);
    }
    
    @Bean
    public PostgresDULProperties jsdDULProperties() {
        return new PostgresDULProperties();
    }
}
